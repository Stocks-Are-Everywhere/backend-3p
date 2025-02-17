package org.scoula.three_people.order.service.datastructure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import org.scoula.three_people.order.constant.OrderConstant;
import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.TradeHistory;
import org.springframework.stereotype.Component;

@Component
public class PriceTreeMap {

	private final TreeMap<Integer, PriceLevel> buyOrders = new TreeMap<>(Comparator.reverseOrder());

	private final TreeMap<Integer, PriceLevel> sellOrders = new TreeMap<>();

	public List<TradeHistory> matchWithBuyOrder(final Order order) {
		PriceLevel level = getBuyPriceLevel(order.getPrice());
		List<TradeHistory> histories = level.match(order);
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

	public List<TradeHistory> matchWithSellOrder(final Order order) {
		PriceLevel level = getSellPriceLevel(order.getPrice());
		List<TradeHistory> histories = level.match(order);
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

	public List<TradeHistory> matchWithMarketSellOrder(final Order order) {
		PriceLevel level = getSellPriceLevel(OrderConstant.MARKET_ORDER_PRICE.getValue());
		return level.match(order);
	}

	public List<TradeHistory> matchWithMarketBuyOrder(final Order order) {
		PriceLevel level = getBuyPriceLevel(OrderConstant.MARKET_ORDER_PRICE.getValue());
		return level.match(order);
	}

	public List<TradeHistory> matchMarketOrderWithSellOrders(final Order order) {
		List<TradeHistory> histories = new ArrayList<>();
		sellOrders.keySet().stream()
			.filter(key -> key != OrderConstant.MARKET_ORDER_PRICE.getValue())
			.forEach(key -> {
				PriceLevel level = getSellPriceLevel(key);
				histories.addAll(level.match(order));
			});
		addRemainingBuyOrder(order);

		return histories;
	}

	public List<TradeHistory> matchMarketOrderWithBuyOrders(final Order order) {
		List<TradeHistory> histories = new ArrayList<>();
		sellOrders.keySet().stream()
			.filter(key -> key != OrderConstant.MARKET_ORDER_PRICE.getValue())
			.forEach(key -> {
				PriceLevel level = getBuyPriceLevel(key);
				histories.addAll(level.match(order));
			});
		addRemainingSellOrder(order);

		return histories;
	}
}
