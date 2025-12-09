package com.system.batch.config;

import com.system.batch.listener.BigBrotherJobExecutionListener;
import com.system.batch.listener.BigBrotherStepExecutionListener;
import com.system.batch.listener.ServerRackControlListener;
import com.system.batch.listener.ServerRoomInfiltrationListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class SystemMonitorConfig {

    @Bean
    public Job monitoringJob(JobRepository jobRepository,
                             Step monitoringStep){

        return new JobBuilder("monitoringJob", jobRepository)
                .listener(new ServerRoomInfiltrationListener())
                .start(monitoringStep)
                .build();

    }

    @Bean
    public Step monitoringStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              Tasklet monitoringTasklet){
        return new StepBuilder("monitoringStep", jobRepository)
                .tasklet(monitoringTasklet, transactionManager)
                .listener(new ServerRackControlListener())
                .build();
    }

    @Bean
    public Tasklet monitoringTasklet(){
        return (contribution, chunkContext) -> {
            log.info("monitoring...");
            return RepeatStatus.FINISHED;
        };

    }
}
