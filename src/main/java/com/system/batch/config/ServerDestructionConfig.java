package com.system.batch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class ServerDestructionConfig {


    @Bean
    public Job killDashNineJob(JobRepository jobRepository,
                               Step terminationStep){
        return new JobBuilder("KillDashNineJob", jobRepository)
                .listener(systemTerminationListener(null))
                .start(terminationStep)
                .build();
    }

    @Bean
    public Step terminationStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager){
        return new StepBuilder("terminationStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("시스템 제거중...");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    @JobScope
    public JobExecutionListener systemTerminationListener(
            @Value("#{jobParameters['terminationType']}") String terminationType
    ){
        return new JobExecutionListener() {

            @Override
            public void beforeJob(JobExecution jobExecution) {
                log.info("시스템 제거 시작!{}", terminationType);
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                log.info("작전 종료 시스템 상태{}", jobExecution.getStatus());
            }
        };
    }
}
