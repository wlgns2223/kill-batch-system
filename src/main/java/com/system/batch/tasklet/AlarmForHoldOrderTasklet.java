package com.system.batch.tasklet;

import com.system.batch.entity.Orders;
import com.system.batch.repository.OrderRepository;
import com.system.batch.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class AlarmForHoldOrderTasklet implements Tasklet {

    private final OrderRepository orderRepository;
    private final AlarmService alarmService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        List<Orders> orders = orderRepository.findAllByStatusIs("on Hold");
        if(orders.isEmpty()){
            log.info("홀딩중인 배송제품 없음");
            return RepeatStatus.FINISHED;
        }

        for(Orders o: orders){
            alarmService.send(String.format("%d is holding",o.getOrderNumber()));
        }

        return RepeatStatus.FINISHED;
    }
}
