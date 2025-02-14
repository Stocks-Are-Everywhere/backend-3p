package org.scoula.three_people.order.service.datastructure;

import org.scoula.three_people.order.domain.Order;

public interface OrderBook {

	void addBuyOrder(Order order);

	void addSellOrder(Order order);

	boolean containsBuyOrder(Order order);

	boolean containsSellOrder(Order order);

	Order peekBuyOrder(String companyCode);

	Order peekSellOrder(String companyCode);

	Order pollBuyOrder(String companyCode);

	Order pollSellOrder(String companyCode);

	boolean hasBuyOrders(String companyCode);

	boolean hasSellOrders(String companyCode);
}
