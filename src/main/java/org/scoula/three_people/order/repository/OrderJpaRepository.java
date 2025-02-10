package org.scoula.three_people.order.repository;

import org.scoula.three_people.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

}
