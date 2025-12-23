package com.system.batch.config;


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

@Configuration
public class HelloWorldJobConfig {
    @Bean
    public Job helloWorldJob(JobRepository jobRepository, Step helloWorldStep){
        return new JobBuilder("helloWorldJob", jobRepository)
                .start(helloWorldStep)
                .build();
    }


    @Bean
    public Step helloWorldStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager
    ){
        return new StepBuilder("helloWorldStep", jobRepository)
                .tasklet(helloWorldTasklet(), platformTransactionManager)
                .build();

    }

    public Tasklet helloWorldTasklet(){
        return (contribution, chunkContext) -> {

            System.out.print("database connecion");
            return RepeatStatus.FINISHED;
        };
    }
}
