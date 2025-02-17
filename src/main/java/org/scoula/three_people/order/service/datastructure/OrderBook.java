package org.scoula.three_people.order.service.datastructure;

import java.util.List;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.TradeHistory;

public interface OrderBook {

	List<TradeHistory> matchWithMarketOrder(Order order);

	List<TradeHistory> matchFixedPrice(Order order);

	List<TradeHistory> matchMarketOrderWithLimitOrders(Order order);
}
