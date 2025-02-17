package org.scoula.three_people.order.service.strategy;

import java.util.ArrayList;
import java.util.List;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.OrderHistory;
import org.scoula.three_people.order.service.datastructure.OrderBook;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class LimitOrderStrategy {

	private final OrderBook orderBook;
	private final ApplicationEventPublisher publisher;

	public List<OrderHistory> process(final Order order) {
		List<OrderHistory> orderHistories = new ArrayList<>();
		orderHistories.addAll(orderBook.matchWithMarketOrder(order));
		orderHistories.addAll(orderBook.matchFixedPrice(order));
		return orderHistories;
	}
}
