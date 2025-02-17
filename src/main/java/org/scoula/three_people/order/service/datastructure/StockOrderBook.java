package org.scoula.three_people.order.service.datastructure;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.TradeHistory;
import org.springframework.stereotype.Component;

@Component
public class StockOrderBook implements OrderBook {

	private final ConcurrentHashMap<String, PriceTreeMap> elements = new ConcurrentHashMap<>();

	@Override
	public List<TradeHistory> matchFixedPrice(final Order order) {
		PriceTreeMap priceTreeMap = getPriceTreeMap(order.getCompanyCode());
		if (order.isBuyType()) {
			return priceTreeMap.matchWithSellOrder(order);
		}
		return priceTreeMap.matchWithBuyOrder(order);
	}

	private PriceTreeMap getPriceTreeMap(String companyCode) {
		PriceTreeMap priceTreeMap = elements.get(companyCode);
		if (priceTreeMap == null) {
			elements.put(companyCode, new PriceTreeMap());
			priceTreeMap = elements.get(companyCode);
		}
		return priceTreeMap;
	}

	@Override
	public List<TradeHistory> matchWithMarketOrder(final Order order) {
		PriceTreeMap priceTreeMap = getPriceTreeMap(order.getCompanyCode());
		if (order.isBuyType()) {
			return priceTreeMap.matchWithMarketSellOrder(order);
		}
		return priceTreeMap.matchWithMarketBuyOrder(order);
	}

	@Override
	public List<TradeHistory> matchMarketOrderWithLimitOrders(Order order) {
		PriceTreeMap priceTreeMap = getPriceTreeMap(order.getCompanyCode());
		if (order.isBuyType()) {
			return priceTreeMap.matchMarketOrderWithSellOrders(order);
		}
		return priceTreeMap.matchMarketOrderWithBuyOrders(order);
	}
}
