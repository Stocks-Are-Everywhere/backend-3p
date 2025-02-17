package org.scoula.three_people.order.repository;

import org.scoula.three_people.order.domain.TradeHistory;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TradeHistoryRepositoryImpl {

	private final TradeHistoryJpaRepository tradeHistoryJpaRepository;

	public TradeHistory save(TradeHistory tradeHistory) {
		return tradeHistoryJpaRepository.save(tradeHistory);
	}
}
