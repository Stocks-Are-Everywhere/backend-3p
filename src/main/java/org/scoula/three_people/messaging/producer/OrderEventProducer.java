package org.scoula.three_people.messaging.producer;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.scoula.three_people.messaging.producer.dto.OrderEventDTO;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@RequiredArgsConstructor
@Slf4j
public class TradeEventProducer {
    private final RocketMQTemplate rocketMQTemplate;
    
    private static final String ORDER_TOPIC = "${ROCKETMQ_ORDER_TOPIC}";
    private static final String ORDER_TAG = "executed";
    
    public void publishOrderEvent(OrderEventDTO event) {
        String destination = ORDER_TOPIC + ":" + ORDER_TAG;
        
        rocketMQTemplate.asyncSend(destination, event, new SendCallback() {
            @Override
            public void onSuccess(SendResult result) {
                log.info("Order event published - sellOrderId: {}, buyOrderId: {}", 
                         event.getSellOrderId(), event.getBuyOrderId());
            }
            
            @Override
            public void onException(Throwable throwable) {
                log.error("Failed to publish event - sellOrderId: {}, buyOrderId: {}", 
                          event.getSellOrderId(), event.getBuyOrderId(), throwable);
            }
        });
     }
}