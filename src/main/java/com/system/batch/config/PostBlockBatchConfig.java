package com.system.batch.config;

import com.system.batch.entity.Post;
import com.system.batch.entity.Report;
import jakarta.persistence.EntityManagerFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PostBlockBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job postBlockBatchJob(Step postBlockStep){
        return new JobBuilder("postBlockBatchJob", jobRepository)
                .start(postBlockStep)
                .build();
    }

    @Bean
    public Step postBlockStep(
            JpaPagingItemReader<Post> postBlockReader,
            PostBlockedProcessor postBlockedProcessor,
            ItemWriter<BlockedPost> postBlockWriter
    ){
        return new StepBuilder("postBlockStep", jobRepository)
                .<Post, BlockedPost>chunk(5, platformTransactionManager)
                .reader(postBlockReader)
                .processor(postBlockedProcessor)
                .writer(postBlockWriter)
                .build();
    }

//    @Bean
//    @StepScope
//    public JpaCursorItemReader<Post> postBlockReader(
//            @Value("#{jobParameters['startDateTime']}") LocalDateTime startDate,
//            @Value("#{jobParameters['endDateTime']}") LocalDateTime endDate
//    ) {
//        return new JpaCursorItemReaderBuilder<Post>()
//                .name("postBlockReader")
//                .entityManagerFactory(entityManagerFactory)
//                .queryString("""
//                            SELECT p FROM Post p JOIN FETCH p.reports r
//                            WHERE r.reportedAt >= :startDate AND r.reportedAt < :endTime
//                        """)
//                .parameterValues(
//                        Map.of("startDate", startDate,
//                                "endDate", endDate))
//                .build();
//
//    }
    @Bean
    @StepScope
    public JpaPagingItemReader<Post> postBlockReader(
            @Value("#{jobParameters['startDateTime']}") LocalDateTime startDate,
            @Value("#{jobParameters['endDateTime']}") LocalDateTime endDate
    ) {
        return new JpaPagingItemReaderBuilder<Post>()
                .name("postBlockReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        SELECT DISTINCT p from Post p
                        JOIN p.reports r
                        where r.reportedAt >= :startDate AND r.reportedAt < :endDate
                        ORDER BY p.id ASC
                        """)
                .parameterValues(
                        Map.of("startDate", startDate,
                                "endDate", endDate))
                .pageSize(5).build();
    }

    @Bean
    public ItemWriter<BlockedPost> postBlockWriter(){
        return chunk -> {
            chunk.forEach(blockedPost -> {
                log.info("💀 TERMINATED: [ID:{}] '{}' by {} | 신고:{}건 | 점수:{} | kill -9 at {}",
                        blockedPost.getPostId(),
                        blockedPost.getTitle(),
                        blockedPost.getWriter(),
                        blockedPost.getReportCount(),
                        String.format("%.2f", blockedPost.getBlockScore()),
                        blockedPost.getBlockedAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

            });
        };
    }

    @Getter
    @Builder
    @ToString
    public static class BlockedPost {
        private Long postId;
        private String writer;
        private String title;
        private int reportCount;
        private double blockScore;
        private LocalDateTime blockedAt;

    }

    @Component
    public static class PostBlockedProcessor implements ItemProcessor<Post, BlockedPost>{
        @Override
        public BlockedPost process(Post item) throws Exception {
            double blockScore = calculateBlockScore(item.getReports());
            if(blockScore >= 7.0){
                return BlockedPost.builder()
                        .postId(item.getId())
                        .writer(item.getWriter())
                        .title(item.getTitle())
                        .reportCount(item.getReports().size())
                        .blockScore(blockScore)
                        .blockedAt(LocalDateTime.now())
                        .build();
            }
            return null;
        }

        private double calculateBlockScore(List<Report> reports){
            return Math.random() * 10;
        }
    }
}
