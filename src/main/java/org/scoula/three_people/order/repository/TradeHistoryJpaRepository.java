package org.scoula.three_people.order.repository;

import org.scoula.three_people.order.domain.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHistoryJpaRepository extends JpaRepository<OrderHistory, Long> {

}
