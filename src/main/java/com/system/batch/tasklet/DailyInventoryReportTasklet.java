package com.system.batch.tasklet;


import com.system.batch.entity.ItemStock;
import com.system.batch.repository.InventoryRepository;
import com.system.batch.service.AlarmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;

@Slf4j
public class DailyInventoryReportTasklet implements Tasklet {

    private final AlarmService alarmService;
    private final InventoryRepository inventoryRepository;

    public DailyInventoryReportTasklet(AlarmService alarmService,InventoryRepository inventoryRepository){
        this.alarmService = alarmService;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<ItemStock> lowStockItems = inventoryRepository.findLowStockItems(10);

        if(lowStockItems.isEmpty()){
            log.info("모든 품목 재고 안정");
            return RepeatStatus.FINISHED;
        }

        StringBuilder message = new StringBuilder("재고 부족 품목 알림\n");

        for(ItemStock item : lowStockItems){
            message.append(String.format("- %s: 재고 %d개 \n", item.getItemName(), item.getItemStock()));
        }

        log.info("재고 부족 리포트 발송");
        alarmService.send(message.toString());
        return RepeatStatus.FINISHED;
    }
}
