package org.scoula.three_people.order.service.datastructure;

import java.util.concurrent.ConcurrentHashMap;
import org.scoula.three_people.order.domain.Order;
import org.springframework.stereotype.Component;

@Component
public class StockOrderBook implements OrderBook {
	private final ConcurrentHashMap<String, PriceMap> elements = new ConcurrentHashMap<>();

	private PriceMap getPriceMap(String companyCode) {
		return elements.computeIfAbsent(companyCode, k -> new PriceTreeMap());
	}

	@Override
	public void addBuyOrder(Order order) {
		getPriceMap(order.getCompanyCode()).addBuyOrder(order);
	}

	@Override
	public void addSellOrder(Order order) {
		getPriceMap(order.getCompanyCode()).addSellOrder(order);
	}

	@Override
	public boolean containsBuyOrder(Order order) {
		return getPriceMap(order.getCompanyCode()).containsBuyOrder(order);
	}

	@Override
	public boolean containsSellOrder(Order order) {
		return getPriceMap(order.getCompanyCode()).containsSellOrder(order);
	}

	@Override
	public Order peekBuyOrder(String companyCode) {
		return getPriceMap(companyCode).peekBuyOrder();
	}

	@Override
	public Order peekSellOrder(String companyCode) {
		return getPriceMap(companyCode).peekSellOrder();
	}

	@Override
	public boolean hasBuyOrders(String companyCode) {
		return getPriceMap(companyCode).hasBuyOrders();
	}

	@Override
	public boolean hasSellOrders(String companyCode) {
		return getPriceMap(companyCode).hasSellOrders();
	}

	@Override
	public boolean hasMarketBuyOrders(String companyCode) {
		return getPriceMap(companyCode).hasMarketBuyOrders();
	}

	@Override
	public boolean hasMarketSellOrders(String companyCode) {
		return getPriceMap(companyCode).hasMarketSellOrders();
	}

	@Override
	public Order peekMarketBuyOrder(String companyCode) {
		return getPriceMap(companyCode).peekMarketBuyOrder();
	}

	@Override
	public Order peekMarketSellOrder(String companyCode) {
		return getPriceMap(companyCode).peekMarketSellOrder();
	}

	@Override
	public Order pollMarketBuyOrder(String companyCode) {
		return getPriceMap(companyCode).pollMarketBuyOrder();
	}

	@Override
	public Order pollMarketSellOrder(String companyCode) {
		return getPriceMap(companyCode).pollMarketSellOrder();
	}

	@Override
	public int getLastTradedPrice(String companyCode) {
		return getPriceMap(companyCode).getLastTradedPrice();
	}

	@Override
	public void setLastTradedPrice(String companyCode, int price) {
		getPriceMap(companyCode).setLastTradedPrice(price);
	}

	@Override
	public void cancelOrder(String companyCode, Order order) {
		getPriceMap(companyCode).cancelOrder(order);
		order.cancel();
	}

	@Override
	public Order pollBuyOrder(String companyCode) {
		return getPriceMap(companyCode).pollBuyOrder();
	}

	@Override
	public Order pollSellOrder(String companyCode) {
		return getPriceMap(companyCode).pollSellOrder();
	}
}
