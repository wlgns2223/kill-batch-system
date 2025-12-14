package com.system.batch.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class SystemFailureWithMultiSource {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job systemFailureJob(Step systemFailureStep){
        return new JobBuilder("systemFailureJobWithMultiSource", jobRepository)
                .start(systemFailureStep)
                .build();
    }

    @Bean
    public Step systemFailureStep(
            MultiResourceItemReader<SystemFailure> multiSystemFailureItemReader,
            SystemFailureStdoutItemWriter systemFailureStdoutItemWriter
    ){
        return new StepBuilder("systemFailureStep", jobRepository)
                .<SystemFailure, SystemFailure>chunk(10, transactionManager)
                .reader(multiSystemFailureItemReader)
                .writer(systemFailureStdoutItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public MultiResourceItemReader<SystemFailure> multiResourceItemReader(
            @Value("#{jobParameters['inputFilePath']}") String inputFilePath
    ){
        return new MultiResourceItemReaderBuilder<SystemFailure>()
                .name("multiSystemFailureItemReader")
                .resources(new Resource[]{
                        new FileSystemResource(inputFilePath + "/critical-failures.csv"),
                        new FileSystemResource(inputFilePath + "/normal-failures.csv")
                })
                .delegate(systemFailureFlatFileItemReader())
                .build();
    }

    @Bean
    public FlatFileItemReader<SystemFailure> systemFailureFlatFileItemReader(){
        return new FlatFileItemReaderBuilder<SystemFailure>()
                .name("systemFailureFileReader")
                .delimited()
                .delimiter(",")
                .names("errorId", "errorDateTime", "severity", "processId", "errorMessage")
                .targetType(SystemFailure.class)
                .linesToSkip(1)
                .build();
    }






    @Bean
    public SystemFailureStdoutItemWriter systemFailureStdoutItemWriter(){
        return new SystemFailureStdoutItemWriter();
    }

    public static class SystemFailureStdoutItemWriter implements ItemWriter<SystemFailure>{
        @Override
        public void write(Chunk<? extends SystemFailure> chunk) throws Exception {
            for(SystemFailure failure: chunk){
                log.info("Processing System Failure: {}",failure);
            }
        }
    }

    @Data
    public static class SystemFailure{
        private String errorId;
        private String errorDateTime;
        private String severity;
        private Integer processId;
        private String errorMessage;
    }


}
