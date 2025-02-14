package org.scoula.three_people.order.service.datastructure;

import java.util.Comparator;
import java.util.PriorityQueue;

import org.scoula.three_people.order.domain.Order;

public class PriceLevel {

	/**
	 * 같은 가격대에서는 "시간 우선" 순서를 보장하기 위해
	 * createdDateTime 기준으로 정렬하는 PriorityQueue 사용
	 */
	private final PriorityQueue<Order> orders =
			new PriorityQueue<>(Comparator.comparing(Order::getCreatedDateTime));

	// 전체 수량 누적용이라면, 로직에 맞춰 따로 계산 필요
	// 여기서는 단순 예시로 0L만 넣고 사용 X
	private final Long totalQuantity = 0L;

	public void addOrder(Order order) {
		orders.add(order);
	}

	public boolean containsOrder(Order order) {
		// 예시로 isMatchable 검사 대신 ID 비교 등으로 변경 가능
		return orders.stream()
				.anyMatch(o -> o.getId().equals(order.getId()));
	}

	public Order peek() {
		return orders.peek();
	}

	public Order poll() {
		return orders.poll();
	}

	public boolean isEmpty() {
		return orders.isEmpty();
	}
}
