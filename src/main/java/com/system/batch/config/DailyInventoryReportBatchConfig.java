package com.system.batch.config;

import com.system.batch.entity.ItemStock;
import com.system.batch.repository.InventoryRepository;
import com.system.batch.service.AlarmService;
import com.system.batch.tasklet.DailyInventoryReportTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
public class DailyInventoryReportBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    public DailyInventoryReportBatchConfig(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
    }

    @Bean
    public Tasklet dailyInventoryReportTasklet(){
        return new DailyInventoryReportTasklet(
                new AlarmService() {
            @Override
            public void send(String message) { }
        },
        new InventoryRepository() {
            @Override
            public List<ItemStock> findLowStockItems(int stock) { return List.of(); }
        });
    }

    @Bean
    public Step dailyInventoryReportStep(){
        return new StepBuilder("dailyInventoryReportStep", jobRepository)
                .tasklet(dailyInventoryReportTasklet(), platformTransactionManager)
                .build();
    }

    @Bean
    public Job dailyInventoryReportJob(){
        return new JobBuilder("dailyInventoryReportJob", jobRepository)
                .start(dailyInventoryReportStep())
                .build();
    }

}
