package org.scoula.three_people.order.service.strategy;

import org.scoula.three_people.order.domain.Order;

public interface OrderStrategy {
    String process(Order order);
}
