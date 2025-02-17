package org.scoula.three_people.order.service;

import java.util.ArrayList;
import java.util.List;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.TradeHistory;
import org.scoula.three_people.order.service.datastructure.OrderBook;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class OrderProcessor {

	private final OrderBook orderBook;

	public List<TradeHistory> process(final Order order) {
		if (order.isMarketOrder()) {
			return processMarketOrder(order);
		}
		return processFixedOrder(order);
	}

	private List<TradeHistory> processMarketOrder(final Order order) {
		return orderBook.matchMarketOrderWithLimitOrders(order);
	}

	private List<TradeHistory> processFixedOrder(final Order order) {
		List<TradeHistory> tradeHistories = new ArrayList<>();
		tradeHistories.addAll(orderBook.matchWithMarketOrder(order));
		tradeHistories.addAll(orderBook.matchFixedPrice(order));
		return tradeHistories;
	}
}
