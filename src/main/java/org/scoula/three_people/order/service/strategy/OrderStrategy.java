package org.scoula.three_people.order.service.strategy;

import java.util.List;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.OrderHistory;

public interface OrderStrategy {
	List<OrderHistory> process(Order order);
}
