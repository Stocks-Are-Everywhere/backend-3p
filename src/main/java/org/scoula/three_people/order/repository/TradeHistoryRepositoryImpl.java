package org.scoula.three_people.order.repository;

import java.util.Collection;
import java.util.List;

import org.scoula.three_people.order.domain.TradeHistory;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TradeHistoryRepositoryImpl {

	private final TradeHistoryJpaRepository tradeHistoryJpaRepository;

	public List<TradeHistory> saveAllHistory(Collection<TradeHistory> tradeHistories) {
		return tradeHistoryJpaRepository.saveAll(tradeHistories);
	}

	public TradeHistory save(TradeHistory tradeHistory) {
		return tradeHistoryJpaRepository.save(tradeHistory);
	}
}
