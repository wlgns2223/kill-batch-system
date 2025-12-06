package com.system.batch.config;

import com.system.batch.tasklet.ZombieProcessCleanupTasklet;
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
public class ZombieBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    public ZombieBatchConfig(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
    }

    @Bean
    public Tasklet zombieProcessCleanupTasklet(){
        return new ZombieProcessCleanupTasklet();
    }

    @Bean
    public Step zombieCleanupStep(){
        return new StepBuilder("zombieCleanupStep", jobRepository)
                .tasklet(zombieProcessCleanupTasklet(), new ResourcelessTransactionManager())
                .build();
    }

    @Bean
    public Job zombieCleanup(){
        return new JobBuilder("zombieCleanupJob", jobRepository)
                .start(zombieCleanupStep())
                .build();
    }
}
