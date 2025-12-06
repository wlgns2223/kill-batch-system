package com.system.batch.config;

import com.system.batch.tasklet.DeleteOldFilesTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class FileCleanupBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    public FileCleanupBatchConfig(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
    }

    @Bean
    public Tasklet deleteOldFilesTasklet(){
        return new DeleteOldFilesTasklet("./temp", 30);
    }

    @Bean
    public Step deleteOldFilesStep(){
        return new StepBuilder("deleteOldFilesStep", jobRepository)
                .tasklet(deleteOldFilesTasklet(), new ResourcelessTransactionManager())
                .build();
    }

    @Bean
    public Job deleteOldFilesJob(){
        return new JobBuilder("deleteOldFilesJob", jobRepository)
                .start(deleteOldFilesStep())
                .build();
    }

}
