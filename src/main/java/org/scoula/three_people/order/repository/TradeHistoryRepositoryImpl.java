package org.scoula.three_people.order.repository;

import org.scoula.three_people.order.domain.OrderHistory;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderHistoryRepositoryImpl {

	private final OrderHistoryJpaRepository orderHistoryJpaRepository;

	public OrderHistory save(OrderHistory orderHistory) {
		return orderHistoryJpaRepository.save(orderHistory);
	}
}
