package org.scoula.three_people.order.event.consumer;

import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.scoula.three_people.order.dto.OrderHistoryDTO;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RocketMQMessageListener(
    topic = "trade-events",                        
    consumerGroup = "trade-persistence-group",     
    selectorExpression = "persistence",            
    consumeMode = ConsumeMode.CONCURRENTLY,
    messageModel = MessageModel.CLUSTERING
)
public class TradePersistenceConsumer implements RocketMQListener<OrderHistoryDTO> {
    @Override
    public void onMessage(OrderHistoryDTO message) {
        log.info("Received trade event - sellOrderId: {}, buyOrderId: {}, quantity: {}, price: {}", 
            message.sellOrderId(), 
            message.buyOrderId(),
            message.quantity(),
            message.price()
        );
    }
}