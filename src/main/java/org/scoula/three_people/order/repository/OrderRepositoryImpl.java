package org.scoula.three_people.order.repository;

import org.scoula.three_people.order.domain.Order;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl {

	private final OrderJpaRepository orderJpaRepository;

	public Order save(Order order) {
		return orderJpaRepository.save(order);
	}
}