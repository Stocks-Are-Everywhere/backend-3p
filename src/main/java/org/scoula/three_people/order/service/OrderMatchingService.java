package org.scoula.three_people.order.service;

import lombok.RequiredArgsConstructor;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.OrderHistory;
import org.scoula.three_people.order.domain.Type;
import org.scoula.three_people.order.dto.OrderHistoryDTO;
import org.scoula.three_people.order.repository.OrderHistoryRepositoryImpl;
import org.scoula.three_people.order.repository.OrderRepositoryImpl;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

@RequiredArgsConstructor
@Service
public class OrderMatchingService {

	private final OrderRepositoryImpl orderRepository;
	private final OrderHistoryRepositoryImpl orderHistoryRepository;
	private final MatchingQueue matchingQueue;

	@Transactional
	public String processOrder(Order order) {
		StringBuilder matchingLog = new StringBuilder();
		logOrderReceived(order, matchingLog);

		if (order.getType() == Type.BUY) {
			matchBuyOrder(order, matchingLog);
		} else {
			matchSellOrder(order, matchingLog);
		}

		return matchingLog.toString();
	}

	//매수 주문을 매칭하여 체결하거나 큐에 추가
	private void matchBuyOrder(Order buyOrder, StringBuilder matchingLog) {
		while (matchingQueue.hasSellOrders() && buyOrder.getRemainingQuantity() > 0) {
			Order sellOrder = matchingQueue.peekSellOrder();

			if (buyOrder.getPrice() < sellOrder.getPrice()) {
				break;
			}

			executeMatch(buyOrder, sellOrder, matchingLog);
		}

		if (!buyOrder.hasNoRemainingQuantity()) {
			matchingQueue.addBuyOrder(buyOrder);
		}
	}

	//매도 주문을 매칭하여 체결하거나 큐에 추가
	private void matchSellOrder(Order sellOrder, StringBuilder matchingLog) {
		while (matchingQueue.hasBuyOrders() && !sellOrder.hasNoRemainingQuantity()) {
			Order buyOrder = matchingQueue.peekBuyOrder();

			if (buyOrder.getPrice() < sellOrder.getPrice()) {
				break;
			}

			executeMatch(buyOrder, sellOrder, matchingLog);
		}

		if (!sellOrder.hasNoRemainingQuantity()) {
			matchingQueue.addSellOrder(sellOrder);
		}
	}

	//주문 체결 후 거래 내역 저장 및 주문 상태 업데이트
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
	}

	private void logOrderReceived(Order order, StringBuilder log) {
		log.append(String.format(
			"%s order received: %s\n",
			order.getType(),
			order
		));
	}

	private void logTradeExecution(Order buyOrder, Order sellOrder,
		int quantity, StringBuilder log) {
		log.append(String.format(
			"Trade executed: %d units at %d (Buy: %d, Sell: %d)\n",
			quantity,
			sellOrder.getPrice(),
			buyOrder.getId(),
			sellOrder.getId()
		));
	}
}

// 매도(낮은 가격 우선) / 매수(높은 가격 우선) 주문 큐
@Component
class MatchingQueue {
	private final PriorityQueue<Order> buyOrders = new PriorityQueue<>(
		Comparator.comparing(Order::getPrice).reversed()
			.thenComparing(Order::getCreatedDateTime)
	);

	private final PriorityQueue<Order> sellOrders = new PriorityQueue<>(
		Comparator.comparing(Order::getPrice)
			.thenComparing(Order::getCreatedDateTime)
	);

	public void addBuyOrder(Order order) {
		buyOrders.add(order);
	}

	public void addSellOrder(Order order) {
		sellOrders.add(order);
	}

	public Order peekBuyOrder() {
		return buyOrders.peek();
	}

	public Order peekSellOrder() {
		return sellOrders.peek();
	}

	public boolean hasBuyOrders() {
		return !buyOrders.isEmpty();
	}

	public boolean hasSellOrders() {
		return !sellOrders.isEmpty();
	}
}
