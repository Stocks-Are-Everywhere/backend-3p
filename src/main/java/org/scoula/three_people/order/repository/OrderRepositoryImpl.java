package org.scoula.three_people.order.repository;

import java.util.Optional;

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

	public Optional<Order> findById(Long id) {
		return orderJpaRepository.findById(id);
	}

	public void delete(Order order) {orderJpaRepository.delete(order);}
}