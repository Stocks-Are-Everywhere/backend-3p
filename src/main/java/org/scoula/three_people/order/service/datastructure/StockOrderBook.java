package org.scoula.three_people.order.service.datastructure;

import java.util.concurrent.ConcurrentHashMap;

import org.scoula.three_people.order.domain.Order;
import org.springframework.stereotype.Component;

@Component
public class StockOrderBook implements OrderBook {

	private final ConcurrentHashMap<String, PriceMap> elements = new ConcurrentHashMap<>();

	@Override
	public void addBuyOrder(Order order) {
		if(elements.contains(order.getCompanyCode())) {
			elements.put(order.getCompanyCode(), new PriceTreeMap());
		}

		if (order.getType().isBuyType()) {
			elements.get(order.getCompanyCode()).addBuyOrder(order);
		}
	}

	@Override
	public void addSellOrder(Order order) {
		if(elements.contains(order.getCompanyCode())) {
			elements.put(order.getCompanyCode(), new PriceTreeMap());
		}
		if (!order.getType().isBuyType()) {
			elements.get(order.getCompanyCode()).addSellOrder(order);
		}
	}

	@Override
	public boolean containsBuyOrder(Order order) {
		return elements.get(order.getCompanyCode()).containsBuyOrder(order);
	}

	@Override
	public boolean containsSellOrder(Order order) {
		return elements.get(order.getCompanyCode()).containsSellOrder(order);
	}

	@Override
	public Order peekBuyOrder(String companyCode) {
		return elements.get(companyCode).peekBuyOrder();
	}

	@Override
	public Order peekSellOrder(String companyCode) {
		return elements.get(companyCode).peekSellOrder();
	}

	@Override
	public Order pollBuyOrder(String companyCode) {
		return elements.get(companyCode).pollBuyOrder();
	}

	@Override
	public Order pollSellOrder(String companyCode) {
		return elements.get(companyCode).pollSellOrder();
	}

	@Override
	public boolean hasBuyOrders(String companyCode) {
		return elements.get(companyCode).hasBuyOrders();
	}

	@Override
	public boolean hasSellOrders(String companyCode) {
		return elements.get(companyCode).hasSellOrders();
	}

}
