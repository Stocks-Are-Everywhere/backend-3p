package org.scoula.three_people.order.event.producer;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.scoula.three_people.order.domain.OrderHistory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TradeEventProducer {
    private final RocketMQTemplate rocketMQTemplate;

    private static final String TRADE_TOPIC = "trade-events";
    private static final String TRADE_PERSISTENCE_TAG = "persistence";

    @Value("${rocketmq.producer.send-message-timeout}")
    private int sendMessageTimeout;

    public TradeEventProducer(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }
    
    public void publishTradeEventForPersistence(OrderHistory orderHistory) {
        String destination = TRADE_TOPIC + ":" + TRADE_PERSISTENCE_TAG;
        rocketMQTemplate.asyncSend(destination, orderHistory, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("===============RocketMQ Producer Check===============");
                log.info("Trade event published for persistence - sellOrderId: {}, buyOrderId: {}", 
                         orderHistory.getSellOrderId(), orderHistory.getBuyOrderId());
            }

            @Override
            public void onException(Throwable e) {
                System.out.println("===============RocketMQ Producer fail Check===============");
                System.out.println(e);
                System.out.println("===============RocketMQ Producer fail Check===============");
                log.error("Failed to publish trade event for persistence - sellOrderId: {}, buyOrderId: {}", 
                         orderHistory.getSellOrderId(), orderHistory.getBuyOrderId(), e);
            }
        }, sendMessageTimeout);
    }
}