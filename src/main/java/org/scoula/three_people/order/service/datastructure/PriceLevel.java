package org.scoula.three_people.order.service.datastructure;

import java.util.Comparator;
import java.util.PriorityQueue;

import org.scoula.three_people.order.domain.Order;

public class PriceLevel {

	private final PriorityQueue<Order> orders = new PriorityQueue<>(Comparator.comparing(Order::getCreatedDateTime));
	private final Long totalQuantity = 0L;

	public void addOrder(Order order) {
		orders.add(order);
	}

	public boolean containsOrder(Order order) {
		return orders.stream()
			.anyMatch(order::isMatchable);
	}

	public Order peek() {
		return orders.peek();
	}

	public Order poll() {
		return orders.poll();
	}
}