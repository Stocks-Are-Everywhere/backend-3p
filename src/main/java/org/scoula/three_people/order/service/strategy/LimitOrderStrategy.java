package org.scoula.three_people.order.service.strategy;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.Type;
import org.scoula.three_people.order.service.datastructure.OrderBook;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class LimitOrderStrategy implements OrderStrategy {

	private final OrderBook orderBook;            // <-- PriceTreeMap 아닌 OrderBook 주입
	private final MatchExecutor matchExecutor;
	private final ApplicationEventPublisher publisher;

	@Transactional
	@Override
	public String process(Order order) {
		StringBuilder matchingLog = new StringBuilder();
		logOrderReceived(order, matchingLog);

		if (order.getType() == Type.BUY) {
			matchBuyOrder(order, matchingLog);
			// 남은 수량이 있다면 시장가 매도와도 매칭
			matchLimitWithMarket(order, matchingLog);
		} else {
			matchSellOrder(order, matchingLog);
			// 남은 수량이 있다면 시장가 매수와도 매칭
			matchLimitWithMarket(order, matchingLog);
		}

		return matchingLog.toString();
	}

	// 지정가인 줄 알았으나 ~~ 가격 측정 방식이 시장가
	private void matchBuyOrder(Order buyOrder, StringBuilder matchingLog) {
		String companyCode = buyOrder.getCompanyCode();

		// 1) 지정가 매수 vs 지정가/시장가 매도(우선순위: 시장가 -> 지정가 최저가)
		//    -> 여기서는 OrderBook에 있는 "SellOrders"를 확인
		while (orderBook.hasSellOrders(companyCode) && buyOrder.getRemainingQuantity() > 0) {
			Order sellOrder = orderBook.peekSellOrder(companyCode); // 최저가 매도
			if (sellOrder == null) break;

			// 지정가 vs 지정가일 경우, buy < sell 이면 체결 불가
			// 시장가 vs 지정가면 가격 비교 없이 체결 가능
			if (!sellOrder.isMarketOrder() && buyOrder.getPrice() < sellOrder.getPrice()) {
				// 매수호가가 매도호가보다 낮으니 체결 불가
				break;
			}

			// 체결 수량 및 체결가 결정
			int matchQuantity = Math.min(buyOrder.getRemainingQuantity(), sellOrder.getRemainingQuantity());
			int matchedPrice = sellOrder.isMarketOrder()
					? buyOrder.getPrice()
					: sellOrder.getPrice();

			matchExecutor.execute(buyOrder, sellOrder, matchQuantity, matchedPrice, matchingLog, orderBook);

			// 매도 주문이 전량 체결되었다면 큐에서 제거
			if (sellOrder.hasNoRemainingQuantity()) {
				orderBook.pollSellOrder(companyCode);
			}

			// 매수 주문이 전량 체결되었다면 종료
			if (buyOrder.hasNoRemainingQuantity()) {
				break;
			}
		}

		// 2) 여전히 잔량이 남아있고, 아직 OrderBook(PriceMap)에 등록되지 않았다면 등록
		if (!buyOrder.hasNoRemainingQuantity() && !orderBook.containsBuyOrder(buyOrder)) {
			orderBook.addBuyOrder(buyOrder);
		}
	}

	private void matchSellOrder(Order sellOrder, StringBuilder matchingLog) {
		String companyCode = sellOrder.getCompanyCode();

		// 1) 지정가 매도 vs 지정가/시장가 매수(우선순위: 시장가 -> 지정가 최고가)
		while (orderBook.hasBuyOrders(companyCode) && sellOrder.getRemainingQuantity() > 0) {
			Order buyOrder = orderBook.peekBuyOrder(companyCode);
			if (buyOrder == null) break;

			// 지정가 vs 지정가에서, 매도 가격 > 매수 가격이면 체결 불가
			if (!buyOrder.isMarketOrder() && buyOrder.getPrice() < sellOrder.getPrice()) {
				break;
			}

			int matchQuantity = Math.min(sellOrder.getRemainingQuantity(), buyOrder.getRemainingQuantity());
			int matchedPrice = buyOrder.isMarketOrder()
					? sellOrder.getPrice()
					: buyOrder.getPrice();

			matchExecutor.execute(buyOrder, sellOrder, matchQuantity, matchedPrice, matchingLog, orderBook);

			// 매수 주문 소진 시 제거
			if (buyOrder.hasNoRemainingQuantity()) {
				orderBook.pollBuyOrder(companyCode);
			}
			// 매도 주문 소진 시 종료
			if (sellOrder.hasNoRemainingQuantity()) {
				break;
			}
		}

		// 2) 잔량이 있고, 아직 미등록이면 등록
		if (!sellOrder.hasNoRemainingQuantity() && !orderBook.containsSellOrder(sellOrder)) {
			orderBook.addSellOrder(sellOrder);
		}
	}

	/**
	 * 지정가 주문과 반대 타입의 "시장가" 주문 간 매칭 로직
	 */
	private void matchLimitWithMarket(Order limitOrder, StringBuilder matchingLog) {
		if (limitOrder.hasNoRemainingQuantity()) {
			return;
		}

		String companyCode = limitOrder.getCompanyCode();

		// limitOrder가 매수면 -> 시장가 매도와 매칭
		if (limitOrder.isBuyType()) { // 시장가 매수인데, 가격 측정 방식이 지정가
			while (orderBook.hasMarketSellOrders(companyCode) && !limitOrder.hasNoRemainingQuantity()) {
				Order marketSell = orderBook.peekMarketSellOrder(companyCode);
				if (marketSell == null) break;

				int matchQuantity = Math.min(limitOrder.getRemainingQuantity(), marketSell.getRemainingQuantity());
				// 시장가 매도 -> 체결가는 limitOrder(지정가)의 price
				int matchedPrice = limitOrder.getPrice();

				matchExecutor.execute(limitOrder, marketSell, matchQuantity, matchedPrice, matchingLog, orderBook);

				if (marketSell.hasNoRemainingQuantity()) {
					orderBook.pollMarketSellOrder(companyCode);
				}
			}
		}
		// limitOrder가 매도면 -> 시장가 매수와 매칭
		else {
			while (orderBook.hasMarketBuyOrders(companyCode) && !limitOrder.hasNoRemainingQuantity()) {
				Order marketBuy = orderBook.peekMarketBuyOrder(companyCode);
				if (marketBuy == null) break;

				int matchQuantity = Math.min(limitOrder.getRemainingQuantity(), marketBuy.getRemainingQuantity());
				// 시장가 매수 -> 체결가는 limitOrder(지정가)의 price
				int matchedPrice = limitOrder.getPrice();

				matchExecutor.execute(marketBuy, limitOrder, matchQuantity, matchedPrice, matchingLog, orderBook);

				if (marketBuy.hasNoRemainingQuantity()) {
					orderBook.pollMarketBuyOrder(companyCode);
				}
			}
		}

		// 3) 남은 잔량이 있다면 OrderBook에 추가
		if (!limitOrder.hasNoRemainingQuantity()) {
			if (limitOrder.isBuyType()) {
				if (!orderBook.containsBuyOrder(limitOrder)) {
					orderBook.addBuyOrder(limitOrder);
				}
			} else {
				if (!orderBook.containsSellOrder(limitOrder)) {
					orderBook.addSellOrder(limitOrder);
				}
			}
		}
	}

	private void logOrderReceived(Order order, StringBuilder log) {
		log.append(String.format("Limit order received: %s\n", order));
	}
}
