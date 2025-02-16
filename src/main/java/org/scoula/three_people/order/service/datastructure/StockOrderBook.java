package org.scoula.three_people.order.service.datastructure;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.OrderHistory;
import org.springframework.stereotype.Component;

@Component
public class StockOrderBook implements OrderBook {

	private final ConcurrentHashMap<String, PriceTreeMap> elements = new ConcurrentHashMap<>();

	@Override
	public boolean hasMatchingMarketOrder(Order order) {
		return false;
	}

	@Override
	public List<OrderHistory> matchFixedPrice(final Order order) {
		PriceTreeMap priceTreeMap = elements.get(order.getCompanyCode());
		if (priceTreeMap == null) {
			elements.put(order.getCompanyCode(), new PriceTreeMap());
			priceTreeMap = elements.get(order.getCompanyCode());
		}
		if (order.isBuyType()) {
			return priceTreeMap.matchWithSellOrder(order);
		}
		return priceTreeMap.matchWithBuyOrder(order);
	}
}
