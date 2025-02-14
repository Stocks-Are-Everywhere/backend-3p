package org.scoula.three_people.order.service.datastructure;

import java.util.TreeMap;

import org.scoula.three_people.order.domain.Order;
import org.springframework.stereotype.Component;

@Component
public class PriceTreeMap implements PriceMap {

	private final TreeMap<Integer, PriceLevel> buyOrders = new TreeMap<>();
	private final TreeMap<Integer, PriceLevel> sellOrders = new TreeMap<>();

	@Override
	public void addBuyOrder(Order order) {
		if (!buyOrders.containsKey(order.getPrice())) {
			buyOrders.put(order.getPrice(), new PriceLevel());
			return;
		}
		PriceLevel level = buyOrders.get(order.getPrice());
		level.addOrder(order);
	}

	@Override
	public void addSellOrder(Order order) {
		if (!sellOrders.containsKey(order.getPrice())) {
			sellOrders.put(order.getPrice(), new PriceLevel());
			return;
		}
		PriceLevel level = sellOrders.get(order.getPrice());
		level.addOrder(order);
	}

	@Override
	public boolean containsBuyOrder(Order order) {
		PriceLevel level = buyOrders.get(order.getPrice());
		return level != null && level.containsOrder(order);
	}

	@Override
	public boolean containsSellOrder(Order order) {
		PriceLevel level = sellOrders.get(order.getPrice());
		return level != null && level.containsOrder(order);
	}

	@Override
	public Order peekBuyOrder() {
		return buyOrders.firstEntry().getValue().peek();
	}

	@Override
	public Order pollBuyOrder() {
		return buyOrders.pollFirstEntry().getValue().poll();
	}

	@Override
	public Order peekSellOrder() {
		return sellOrders.firstEntry().getValue().peek();
	}

	@Override
	public Order pollSellOrder() {
		return sellOrders.pollFirstEntry().getValue().poll();
	}

	@Override
	public boolean hasBuyOrders() {
		return !buyOrders.isEmpty();
	}

	@Override
	public boolean hasSellOrders() {
		return !sellOrders.isEmpty();
	}
}