package org.scoula.three_people.order.service.strategy;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.Type;
import org.scoula.three_people.order.repository.OrderHistoryRepositoryImpl;
import org.scoula.three_people.order.repository.OrderRepositoryImpl;
import org.scoula.three_people.order.service.datastructure.OrderBook;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class MarketOrderStrategy implements OrderStrategy {

	private final OrderRepositoryImpl orderRepository;
	private final OrderHistoryRepositoryImpl orderHistoryRepository;
	private final OrderBook orderBook;     // <-- 여러 종목 관리
	private final MatchExecutor matchExecutor;
	private final ApplicationEventPublisher publisher;

	@Transactional
	@Override
	public String process(Order order) {
		StringBuilder matchingLog = new StringBuilder();
		logOrderReceived(order, matchingLog);

		if (order.getType() == Type.BUY) {
			matchMarketBuy(order, matchingLog);
		} else {
			matchMarketSell(order, matchingLog);
		}

		return matchingLog.toString();
	}

	private void matchMarketBuy(Order buyOrder, StringBuilder matchingLog) {
		String companyCode = buyOrder.getCompanyCode();

		// 1) 먼저 "매도 지정가"와 매칭 (있다면)
		while (orderBook.hasSellOrders(companyCode) && buyOrder.getRemainingQuantity() > 0) {
			Order sellOrder = orderBook.peekSellOrder(companyCode);
			if (sellOrder == null) break;

			int matchQuantity = Math.min(buyOrder.getRemainingQuantity(), sellOrder.getRemainingQuantity());
			// 매칭 가격은 매도측이 시장가면 마지막 체결가를 사용, 아니라면 sellOrder.getPrice()
			int matchedPrice = sellOrder.isMarketOrder()
					? getMarketPriceForMarketOrders(companyCode)
					: sellOrder.getPrice();

			// 실제 시장가는 "어떤 가격이든" 수용하지만,
			// 혹시 matchedPrice == 0(직전 체결가가 없다) 이면 체결이 불가능하다고 볼 수도 있음
			if (sellOrder.isMarketOrder() && matchedPrice == 0) {
				// 직전 체결가가 없어 시장가 주문끼리 매칭 불가능
				break;
			}

			matchExecutor.execute(buyOrder, sellOrder, matchQuantity, matchedPrice, matchingLog, orderBook);

			if (sellOrder.hasNoRemainingQuantity()) {
				orderBook.pollSellOrder(companyCode);
			}
		}

		// 2) 아직 잔량이 있고, "매도 시장가"와 매칭
		while (orderBook.hasMarketSellOrders(companyCode) && buyOrder.getRemainingQuantity() > 0) {
			Order marketSell = orderBook.peekMarketSellOrder(companyCode);
			if (marketSell == null) break;

			int matchQuantity = Math.min(buyOrder.getRemainingQuantity(), marketSell.getRemainingQuantity());
			int matchedPrice = getMarketPriceForMarketOrders(companyCode);

			if (matchedPrice == 0) {
				// 아직 기준가가 없다면 매칭 불가능 -> 잔량 남김
				break;
			}

			matchExecutor.execute(buyOrder, marketSell, matchQuantity, matchedPrice, matchingLog, orderBook);

			if (marketSell.hasNoRemainingQuantity()) {
				orderBook.pollMarketSellOrder(companyCode);
			}
		}

		// 3) 잔량이 남으면 시장가 큐에 추가
		if (!buyOrder.hasNoRemainingQuantity()) {
			orderBook.addMarketBuyOrder(buyOrder);
		}
	}

	private void matchMarketSell(Order sellOrder, StringBuilder matchingLog) {
		String companyCode = sellOrder.getCompanyCode();

		// 1) 매수 지정가와 매칭
		while (orderBook.hasBuyOrders(companyCode) && sellOrder.getRemainingQuantity() > 0) {
			Order buyOrder = orderBook.peekBuyOrder(companyCode);
			if (buyOrder == null) break;

			int matchQuantity = Math.min(sellOrder.getRemainingQuantity(), buyOrder.getRemainingQuantity());
			// 매칭 가격: 매수측이 시장가면 lastTradedPrice, 아니면 buyOrder의 지정가
			int matchedPrice = buyOrder.isMarketOrder()
					? getMarketPriceForMarketOrders(companyCode)
					: buyOrder.getPrice();

			if (buyOrder.isMarketOrder() && matchedPrice == 0) {
				break;
			}

			matchExecutor.execute(buyOrder, sellOrder, matchQuantity, matchedPrice, matchingLog, orderBook);

			if (buyOrder.hasNoRemainingQuantity()) {
				orderBook.pollBuyOrder(companyCode);
			}
		}

		// 2) 매수 시장가와 매칭
		while (orderBook.hasMarketBuyOrders(companyCode) && sellOrder.getRemainingQuantity() > 0) {
			Order marketBuy = orderBook.peekMarketBuyOrder(companyCode);
			if (marketBuy == null) break;

			int matchQuantity = Math.min(sellOrder.getRemainingQuantity(), marketBuy.getRemainingQuantity());
			int matchedPrice = getMarketPriceForMarketOrders(companyCode);

			if (matchedPrice == 0) {
				break;
			}

			matchExecutor.execute(marketBuy, sellOrder, matchQuantity, matchedPrice, matchingLog, orderBook);

			if (marketBuy.hasNoRemainingQuantity()) {
				orderBook.pollMarketBuyOrder(companyCode);
			}
		}

		// 3) 잔량이 남으면 시장가 큐에 추가
		if (!sellOrder.hasNoRemainingQuantity()) {
			orderBook.addMarketSellOrder(sellOrder);
		}
	}

	/**
	 * 시장가 주문 vs 시장가 주문 매칭 시 사용할 가격(=직전 체결가)
	 */
	private int getMarketPriceForMarketOrders(String companyCode) {
		// StockOrderBook 을 통해 각 종목별 마지막 체결가 가져오기
		return orderBook.getLastTradedPrice(companyCode);
	}

	private void logOrderReceived(Order order, StringBuilder log) {
		log.append(String.format("Market order received: %s\n", order));
	}
}
