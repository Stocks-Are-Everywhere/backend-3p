package org.scoula.three_people.order.service.strategy;

import lombok.RequiredArgsConstructor;
import org.scoula.three_people.order.domain.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderProcessor {

    private final LimitOrderStrategy limitOrderStrategy;
    private final MarketOrderStrategy marketOrderStrategy;

    public String processOrder(Order order) {
        // 가격이 0원이면 시장가 주문으로 판단
        if (isMarketOrder(order)) {
            return marketOrderStrategy.process(order);
        } else {
            return limitOrderStrategy.process(order);
        }
    }

    private boolean isMarketOrder(Order order) {
        return order.getPrice() == 0;
    }
}
