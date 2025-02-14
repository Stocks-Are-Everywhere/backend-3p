package org.scoula.three_people.order.service.strategy;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.OrderHistory;
import org.scoula.three_people.order.domain.Type;
import org.scoula.three_people.order.dto.OrderHistoryDTO;
import org.scoula.three_people.order.repository.OrderHistoryRepositoryImpl;
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
	private final OrderHistoryRepositoryImpl orderHistoryRepository;
	private final MatchingQueue matchingQueue;
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
			if (buyOrder.getPrice() < sellOrder.getPrice())
				break;
			executeMatch(buyOrder, sellOrder, matchingLog);
		}

		if (!buyOrder.hasNoRemainingQuantity() && !matchingQueue.containsBuyOrder(buyOrder)) {
			matchingQueue.addBuyOrder(buyOrder);
		}
	}

	private void matchSellOrder(Order sellOrder, StringBuilder matchingLog) {
		while (matchingQueue.hasBuyOrders() && !sellOrder.hasNoRemainingQuantity()) {
			Order buyOrder = matchingQueue.peekBuyOrder();
			if (buyOrder.getPrice() < sellOrder.getPrice())
				break;
			executeMatch(buyOrder, sellOrder, matchingLog);
		}

		if (!sellOrder.hasNoRemainingQuantity() && !matchingQueue.containsSellOrder(sellOrder)) {
			matchingQueue.addSellOrder(sellOrder);
		}
	}

	private void executeMatch(Order buyOrder, Order sellOrder, StringBuilder matchingLog) {
		int matchQuantity = Math.min(buyOrder.getRemainingQuantity(), sellOrder.getRemainingQuantity());
		buyOrder.reduceQuantity(matchQuantity);
		sellOrder.reduceQuantity(matchQuantity);

		saveTradeHistory(buyOrder, sellOrder, matchQuantity);
		logTradeExecution(buyOrder, sellOrder, matchQuantity, matchingLog);

		updateOrderStatus(buyOrder);
		updateOrderStatus(sellOrder);
	}

	private void updateOrderStatus(Order order) {
		if (order.hasNoRemainingQuantity()) {
			order.complete();
			orderRepository.save(order);
		}
	}

	private void saveTradeHistory(Order buyOrder, Order sellOrder, int quantity) {
		OrderHistory orderHistory = OrderHistoryDTO.builder()
			.sellOrderId(sellOrder.getId())
			.buyOrderId(buyOrder.getId())
			.quantity(quantity)
			.price(sellOrder.getPrice())
			.build()
			.toEntity();

		orderHistoryRepository.save(orderHistory);
		publisher.publishEvent(orderHistory);
	}

	private void logOrderReceived(Order order, StringBuilder log) {
		log.append(String.format("Limit order received: %s\n", order));
	}

	private void logTradeExecution(Order buyOrder, Order sellOrder, int quantity, StringBuilder log) {
		log.append(String.format("Trade executed: %d units at %d (Buy: %d, Sell: %d)\n",
			quantity, sellOrder.getPrice(), buyOrder.getId(), sellOrder.getId()));
	}
}
