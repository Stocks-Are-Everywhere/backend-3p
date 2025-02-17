package org.scoula.three_people.order.repository;

import org.scoula.three_people.order.domain.TradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeHistoryJpaRepository extends JpaRepository<TradeHistory, Long> {

}
