package org.scoula.three_people.order.service.datastructure;

import org.scoula.three_people.order.domain.Order;

public interface PriceMap {

	// =============== (지정가) 매수/매도 등록 ===============
	void addBuyOrder(Order order);
	void addSellOrder(Order order);

	// =============== (지정가) 포함 여부 확인 ===============
	boolean containsBuyOrder(Order order);
	boolean containsSellOrder(Order order);

	// =============== (지정가) 우선 호가 확인/추출 ===============
	Order peekBuyOrder();
	Order peekSellOrder();
	Order pollBuyOrder();
	Order pollSellOrder();

	// =============== (지정가) 존재 여부 ===============
	boolean hasBuyOrders();
	boolean hasSellOrders();

	// =============== (시장가) 추가 ===============
	void addMarketBuyOrder(String companyCode, Order order);
	void addMarketSellOrder(String companyCode, Order order);

	// =============== (시장가) 확인/추출 ===============
	Order peekMarketBuyOrder();
	Order peekMarketSellOrder();
	Order pollMarketBuyOrder();
	Order pollMarketSellOrder();

	// =============== (시장가) 존재 여부 ===============
	boolean hasMarketBuyOrders();
	boolean hasMarketSellOrders();

	// =============== (마지막 체결가) ===============
	int getLastTradedPrice();
	void setLastTradedPrice(int price);

	// =============== (주문 취소) ===============
	void cancelOrder(Order order);
}
