package org.scoula.three_people.order.service.strategy;

import lombok.RequiredArgsConstructor;
import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.Type;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderProcessor {
    private final LimitOrderStrategy limitOrderStrategy;
    private final MarketOrderStrategy marketOrderStrategy;

    public String processOrder(Order order) {
        if (order.getPrice() == null) {
            return marketOrderStrategy.process(order);
        } else {
            return limitOrderStrategy.process(order);
        }
    }
}
