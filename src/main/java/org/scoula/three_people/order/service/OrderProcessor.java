package org.scoula.three_people.order.service;

import java.util.ArrayList;
import java.util.List;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.OrderHistory;
import org.scoula.three_people.order.service.datastructure.OrderBook;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class OrderProcessor {

	private final OrderBook orderBook;

	public List<OrderHistory> process(final Order order) {
		if (order.isMarketOrder()) {
			return processMarketOrder(order);
		}
		return processFixedOrder(order);
	}

	private List<OrderHistory> processMarketOrder(final Order order) {
		List<OrderHistory> orderHistories = new ArrayList<>();
		orderHistories.addAll(orderBook.matchWithMarketOrder(order));
		orderHistories.addAll(orderBook.matchFixedPrice(order));
		return orderHistories;
	}

	private List<OrderHistory> processFixedOrder(final Order order) {
		List<OrderHistory> orderHistories = new ArrayList<>();
		orderHistories.addAll(orderBook.matchWithMarketOrder(order));
		orderHistories.addAll(orderBook.matchFixedPrice(order));
		return orderHistories;
	}
}
