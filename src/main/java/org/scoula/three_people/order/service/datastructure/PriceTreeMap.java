package org.scoula.three_people.order.service.datastructure;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import org.scoula.three_people.order.constant.OrderConstant;
import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.OrderHistory;
import org.springframework.stereotype.Component;

@Component
public class PriceTreeMap {

	private final TreeMap<Integer, PriceLevel> buyOrders = new TreeMap<>(Comparator.reverseOrder());

	private final TreeMap<Integer, PriceLevel> sellOrders = new TreeMap<>();

	public List<OrderHistory> matchWithBuyOrder(final Order order) {
		PriceLevel level = getBuyPriceLevel(order.getPrice());
		List<OrderHistory> histories = level.match(order);
		addRemainingSellOrder(order);
		return histories;
	}

	private void addRemainingSellOrder(final Order order) {
		if (order.isComplete()) {
			return;
		}
		PriceLevel level = getSellPriceLevel(order.getPrice());
		level.addOrder(order);
	}

	public List<OrderHistory> matchWithSellOrder(final Order order) {
		PriceLevel level = getSellPriceLevel(order.getPrice());
		List<OrderHistory> histories = level.match(order);
		addRemainingBuyOrder(order);
		return histories;
	}

	private void addRemainingBuyOrder(final Order order) {
		if (order.isComplete()) {
			return;
		}
		PriceLevel level = getBuyPriceLevel(order.getPrice());
		level.addOrder(order);
	}

	private PriceLevel getBuyPriceLevel(int price) {
		PriceLevel level = buyOrders.get(price);
		if (level == null) {
			buyOrders.put(price, new PriceLevel());
			level = buyOrders.get(price);
		}
		return level;
	}

	private PriceLevel getSellPriceLevel(int price) {
		PriceLevel level = sellOrders.get(price);
		if (level == null) {
			sellOrders.put(price, new PriceLevel());
			level = sellOrders.get(price);
		}
		return level;
	}

	public List<OrderHistory> matchWithMarketSellOrder(final Order order) {
		PriceLevel level = getSellPriceLevel(OrderConstant.MARKET_ORDER_PRICE.getValue());
		List<OrderHistory> histories = level.match(order);
		addRemainingBuyOrder(order);
		return histories;
	}

	public List<OrderHistory> matchWithMarketBuyOrder(final Order order) {
		PriceLevel level = getBuyPriceLevel(OrderConstant.MARKET_ORDER_PRICE.getValue());
		List<OrderHistory> histories = level.match(order);
		addRemainingSellOrder(order);
		return histories;
	}
}
