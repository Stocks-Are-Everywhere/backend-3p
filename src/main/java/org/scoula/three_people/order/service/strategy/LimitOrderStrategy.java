package org.scoula.three_people.order.service.strategy;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.service.datastructure.OrderBook;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class LimitOrderStrategy {

	private final OrderBook orderBook;
	private final ApplicationEventPublisher publisher;

	public String process(Order order) {
		StringBuilder matchingLog = new StringBuilder();
		return matchingLog.toString();
	}
}
