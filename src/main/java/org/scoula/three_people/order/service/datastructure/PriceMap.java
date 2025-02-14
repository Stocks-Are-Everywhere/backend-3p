package org.scoula.three_people.order.service.datastructure;

import org.scoula.three_people.order.domain.Order;

public interface PriceMap {

	void addBuyOrder(Order order);

	void addSellOrder(Order order);

	boolean containsBuyOrder(Order order);

	boolean containsSellOrder(Order order);

	Order peekBuyOrder();

	Order peekSellOrder();

	Order pollBuyOrder();

	Order pollSellOrder();

	boolean hasBuyOrders();

	boolean hasSellOrders();
}
