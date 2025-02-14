package org.scoula.three_people.order.service.strategy;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.Type;
import org.scoula.three_people.order.repository.OrderRepositoryImpl;
import org.scoula.three_people.order.service.MatchingQueue;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class LimitOrderStrategy implements OrderStrategy {

	private final OrderRepositoryImpl orderRepository;
	private final MatchingQueue matchingQueue;
	private final MatchExecutor matchExecutor;
	private final ApplicationEventPublisher publisher;

	@Transactional
	@Override
	public String process(Order order) {
		StringBuilder matchingLog = new StringBuilder();
		logOrderReceived(order, matchingLog);

		if (order.getType() == Type.BUY) {
			matchBuyOrder(order, matchingLog);
		} else {
			matchSellOrder(order, matchingLog);
		}

		return matchingLog.toString();
	}

	private void matchBuyOrder(Order buyOrder, StringBuilder matchingLog) {
		while (matchingQueue.hasSellOrders() && buyOrder.getRemainingQuantity() > 0) {
			Order sellOrder = matchingQueue.peekSellOrder();

			if (buyOrder.getPrice() < sellOrder.getPrice()) {
				break;
			}

			executeMatch(buyOrder, sellOrder, matchingLog);

			if (sellOrder.hasNoRemainingQuantity()) {
				matchingQueue.pollSellOrder();
			}
		}

		if (!buyOrder.hasNoRemainingQuantity() && !matchingQueue.containsBuyOrder(buyOrder)) {
			matchingQueue.addBuyOrder(buyOrder);
		}
	}

	private void matchSellOrder(Order sellOrder, StringBuilder matchingLog) {
		while (matchingQueue.hasBuyOrders() && !sellOrder.hasNoRemainingQuantity()) {
			Order buyOrder = matchingQueue.peekBuyOrder();

			if (buyOrder.getPrice() < sellOrder.getPrice()) {
				break;
			}

			executeMatch(buyOrder, sellOrder, matchingLog);

			if (buyOrder.hasNoRemainingQuantity()) {
				matchingQueue.pollBuyOrder();
			}
		}

		if (!sellOrder.hasNoRemainingQuantity() && !matchingQueue.containsSellOrder(sellOrder)) {
			matchingQueue.addSellOrder(sellOrder);
		}
	}

	private void executeMatch(Order buyOrder, Order sellOrder, StringBuilder matchingLog) {
		int matchQuantity = Math.min(buyOrder.getRemainingQuantity(), sellOrder.getRemainingQuantity());
		int matchedPrice = sellOrder.getPrice();

		matchExecutor.execute(buyOrder, sellOrder, matchQuantity, matchedPrice, matchingLog);
	}

	private void logOrderReceived(Order order, StringBuilder log) {
		log.append(String.format("Limit order received: %s\n", order));
	}
}
