package org.scoula.three_people.order.service.strategy;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.Type;
import org.scoula.three_people.order.repository.OrderRepositoryImpl;
import org.scoula.three_people.order.service.datastructure.PriceTreeMap;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class LimitOrderStrategy implements OrderStrategy {

	private final OrderRepositoryImpl orderRepository;
	private final PriceTreeMap priceTreeMap;
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
		while (priceTreeMap.hasSellOrders() && buyOrder.getRemainingQuantity() > 0) {
			Order sellOrder = priceTreeMap.peekSellOrder();

			if (buyOrder.getPrice() < sellOrder.getPrice()) {
				break;
			}

			executeMatch(buyOrder, sellOrder, matchingLog);

			if (sellOrder.hasNoRemainingQuantity()) {
				priceTreeMap.pollSellOrder();
			}
		}

		if (!buyOrder.hasNoRemainingQuantity() && !priceTreeMap.containsBuyOrder(buyOrder)) {
			priceTreeMap.addBuyOrder(buyOrder);
		}
	}

	private void matchSellOrder(Order sellOrder, StringBuilder matchingLog) {
		while (priceTreeMap.hasBuyOrders() && !sellOrder.hasNoRemainingQuantity()) {
			Order buyOrder = priceTreeMap.peekBuyOrder();

			if (buyOrder.getPrice() < sellOrder.getPrice()) {
				break;
			}

			executeMatch(buyOrder, sellOrder, matchingLog);

			if (buyOrder.hasNoRemainingQuantity()) {
				priceTreeMap.pollBuyOrder();
			}
		}

		if (!sellOrder.hasNoRemainingQuantity() && !priceTreeMap.containsSellOrder(sellOrder)) {
			priceTreeMap.addSellOrder(sellOrder);
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
