package org.scoula.three_people.order.service.datastructure;

import java.util.List;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.OrderHistory;

public interface OrderBook {

	boolean hasMatchingMarketOrder(Order order);

	List<OrderHistory> matchFixedPrice(Order order);
}
