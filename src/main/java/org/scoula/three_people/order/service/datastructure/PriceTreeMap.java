package org.scoula.three_people.order.service.datastructure;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.OrderHistory;
import org.springframework.stereotype.Component;

@Component
public class PriceTreeMap {

	private final TreeMap<Integer, PriceLevel> buyOrders = new TreeMap<>(Comparator.reverseOrder());

	private final TreeMap<Integer, PriceLevel> sellOrders = new TreeMap<>();

	public List<OrderHistory> matchWithBuyOrder(final Order order) {
		PriceLevel level = buyOrders.get(order.getPrice());
		if (level == null) {
			buyOrders.put(order.getPrice(), new PriceLevel());
			level = buyOrders.get(order.getPrice());
		}
		List<OrderHistory> histories = level.match(order);
		addRemainingSellOrder(order);
		return histories;
	}

	private void addRemainingSellOrder(final Order order) {
		if (order.isComplete()) {
			return;
		}
		PriceLevel level = sellOrders.get(order.getPrice());
		if (level == null) {
			sellOrders.put(order.getPrice(), new PriceLevel());
			level = sellOrders.get(order.getPrice());
		}
		level.addOrder(order);
	}

	public List<OrderHistory> matchWithSellOrder(final Order order) {
		PriceLevel level = sellOrders.get(order.getPrice());
		if (level == null) {
			sellOrders.put(order.getPrice(), new PriceLevel());
			level = sellOrders.get(order.getPrice());
		}
		List<OrderHistory> histories = level.match(order);
		addRemainingBuyOrder(order);
		return histories;
	}

	private void addRemainingBuyOrder(final Order order) {
		if (order.isComplete()) {
			return;
		}
		PriceLevel level = buyOrders.get(order.getPrice());
		if (level == null) {
			buyOrders.put(order.getPrice(), new PriceLevel());
			level = buyOrders.get(order.getPrice());
		}
		level.addOrder(order);
	}
}
