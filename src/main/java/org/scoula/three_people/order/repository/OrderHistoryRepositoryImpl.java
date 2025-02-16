package org.scoula.three_people.order.repository;

import java.util.Collection;
import java.util.List;

import org.scoula.three_people.order.domain.OrderHistory;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderHistoryRepositoryImpl {

	private final OrderHistoryJpaRepository orderHistoryJpaRepository;

	public List<OrderHistory> saveAllHistory(Collection<OrderHistory> orderHistories) {
		return orderHistoryJpaRepository.saveAll(orderHistories);
	}
}
