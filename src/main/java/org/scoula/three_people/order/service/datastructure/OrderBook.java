package org.scoula.three_people.order.service.datastructure;

import org.scoula.three_people.order.domain.Order;

/**
 * Interface defining operations for the order book, ensuring proper integration with StockOrderBook.
 */
public interface OrderBook {
	// ===== Limit Order Operations =====
	void addBuyOrder(Order order);
	void addSellOrder(Order order);
	boolean containsBuyOrder(Order order);
	boolean containsSellOrder(Order order);
	Order peekBuyOrder(String companyCode);
	Order peekSellOrder(String companyCode);
	Order pollBuyOrder(String companyCode); // Required implementation
	Order pollSellOrder(String companyCode); // Required implementation
	boolean hasBuyOrders(String companyCode);
	boolean hasSellOrders(String companyCode);

	// ===== Market Order Operations =====
	boolean hasMarketBuyOrders(String companyCode);
	boolean hasMarketSellOrders(String companyCode);
	Order peekMarketBuyOrder(String companyCode);
	Order peekMarketSellOrder(String companyCode);
	Order pollMarketBuyOrder(String companyCode);
	Order pollMarketSellOrder(String companyCode);

	// ===== Price Tracking =====
	int getLastTradedPrice(String companyCode);
	void setLastTradedPrice(String companyCode, int price);

	// ===== Order Management =====
	void cancelOrder(String companyCode, Order order);

	// Ensure StockOrderBook implements all methods from this interface.
}
