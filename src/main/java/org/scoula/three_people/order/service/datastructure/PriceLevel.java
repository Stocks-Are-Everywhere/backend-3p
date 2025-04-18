package org.scoula.three_people.order.service.datastructure;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.TradeHistory;
import org.scoula.three_people.order.websocket.dto.TradeExecutionMessage;
import org.scoula.three_people.order.websocket.handler.TradeWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PriceLevel {
	private final PriorityQueue<Order> elements = new PriorityQueue<>(Comparator.comparing(Order::getCreatedDateTime));
	private static TradeWebSocketHandler webSocketHandler;

   @Autowired
    public void setWebSocketHandler(TradeWebSocketHandler handler) {
        PriceLevel.webSocketHandler = handler;
    }


	public List<TradeHistory> match(final Order order) {
		List<TradeHistory> history = new ArrayList<>();
		int size = elements.size();
		for (int i = 0; i < size; i++) {
			Order element = elements.poll();
			history.add(processMatching(order, element));
			if (order.hasNoRemainingQuantity()) {
				order.complete();
				if (element.hasNoRemainingQuantity()) {
					element.complete();
					break;
				}
				elements.offer(element);
				break;
			}
			element.complete();
		}
		return history;
	}

	private TradeHistory processMatching(final Order order, final Order element) {
		int quantity = Math.min(order.getRemainingQuantity(), element.getRemainingQuantity());
		order.reduceQuantity(quantity);
		element.reduceQuantity(quantity);
		return createHistory(order, element, quantity);
	}

	private TradeHistory createHistory(final Order order, final Order matchingOrder, int quantity) {
		// TODO: ROcketMQ PR merge 후 event producer 패키지로 이동
		TradeExecutionMessage message = TradeExecutionMessage.builder()
			.price(order.getPrice())
			.quantity(quantity)
			.tradeDateTime(LocalDateTime.now())
			.build();
		webSocketHandler.broadcastTradeExecution(message);

		if (order.isBuyType()) {
			return TradeHistory.builder()
				.sellOrderId(matchingOrder.getId())
				.buyOrderId(order.getId())
				.price(Math.max(order.getPrice(), matchingOrder.getPrice()))
				.quantity(quantity)
				.tradeDateTime(LocalDateTime.now())
				.build();
		}
		return TradeHistory.builder()
			.sellOrderId(order.getId())
			.buyOrderId(matchingOrder.getId())
			.price(Math.max(order.getPrice(), matchingOrder.getPrice()))
			.quantity(quantity)
			.tradeDateTime(LocalDateTime.now())
			.build();
	}

	public void addOrder(final Order order) {
		elements.add(order);
	}
}
