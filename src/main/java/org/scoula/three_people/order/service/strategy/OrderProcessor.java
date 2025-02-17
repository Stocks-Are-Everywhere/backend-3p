package org.scoula.three_people.order.service.strategy;

import java.util.List;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.OrderHistory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderProcessor {

	private final LimitOrderStrategy limitOrderStrategy;
	private final MarketOrderStrategy marketOrderStrategy;

	public List<OrderHistory> process(final Order order) {
		if (order.isMarketOrder()) {
			return marketOrderStrategy.process(order);
		}
		return limitOrderStrategy.process(order);
	}
}
