package com.system.batch.config;

import com.system.batch.validator.SystemDestructionValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class SystemDestructionConfig {

    @Bean
    public Job destructionJob(
            JobRepository jobRepository,
            Step destructionStep,
            SystemDestructionValidator validator
    ){
        return new JobBuilder("destructionJob", jobRepository)
                .start(destructionStep)
                .validator(validator)
                .build();
    }


    @Bean
    public Step destructionStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                Tasklet destructionTasklet){
        return new StepBuilder("destructionStep", jobRepository)
                .tasklet(destructionTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet destructionTasklet(
            @Value("#{jobParameters['destructionPower']}")
            Long destructionLevel
    ){
        return (contribution, chunkContext) -> {

            log.info("destruction power: {}", destructionLevel);

            return RepeatStatus.FINISHED;
        };
    }
}
