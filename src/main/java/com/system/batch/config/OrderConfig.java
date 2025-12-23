package com.system.batch.config;

import com.system.batch.repository.OrderRepository;
import com.system.batch.service.AlarmService;
import com.system.batch.tasklet.AlarmForHoldOrderTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class OrderConfig {

    private final OrderRepository orderRepository;
    private final AlarmService alarmService;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean

    public Tasklet sendAlarmForHoldTasklet() {
        return new AlarmForHoldOrderTasklet(orderRepository, alarmService);
    }

    @Bean
    public Step sendAlarmForHoldStep() {
        return new StepBuilder("sendAlarmForHoldStep", jobRepository)
                .tasklet(sendAlarmForHoldTasklet(), platformTransactionManager)
                .build();
    }

    @Bean
    public Job sendAlarmForHoldJob(){
        return new JobBuilder("sendAlarmForHoldJob", jobRepository)
                .start(sendAlarmForHoldStep())
                .build();
    }

}
