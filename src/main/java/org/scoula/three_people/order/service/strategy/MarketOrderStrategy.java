package org.scoula.three_people.order.service.strategy;

import java.util.List;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.OrderHistory;
import org.scoula.three_people.order.service.datastructure.OrderBook;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class MarketOrderStrategy implements OrderStrategy {

	private final OrderBook orderBook;
	private final ApplicationEventPublisher publisher;

	@Transactional
	@Override
	public List<OrderHistory> process(final Order order) {
		return orderBook.matchMarketOrderWithLimitOrders(order);
	}
}
