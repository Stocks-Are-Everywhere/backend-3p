package org.scoula.three_people.order.service.strategy;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.TradeHistory;
import org.scoula.three_people.order.dto.TradeHistoryDTO;
import org.scoula.three_people.order.repository.TradeHistoryRepositoryImpl;
import org.scoula.three_people.order.repository.OrderRepositoryImpl;
import org.scoula.three_people.order.service.MatchingQueue;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class MarketOrderStrategy implements OrderStrategy {

	private final OrderRepositoryImpl orderRepository;
	private final TradeHistoryRepositoryImpl tradeHistoryRepository;
	private final MatchingQueue matchingQueue;
	private final ApplicationEventPublisher publisher;

	@Transactional
	@Override
	public String process(Order order) {
		StringBuilder matchingLog = new StringBuilder();
		logOrderReceived(order, matchingLog);

		// 매도 주문이 없으면 예외 처리
		if (!matchingQueue.hasSellOrders()) {
			throw new IllegalStateException("No available sell orders for market order.");
		}

		// 시장 가격 결정 (가장 낮은 매도 주문 가격 사용)
		int marketPrice = determineMarketPrice();
		order.setPrice(marketPrice); // 시장가 주문의 price 필드에 시장 가격 설정

		while (matchingQueue.hasSellOrders() && order.getRemainingQuantity() > 0) {
			Order sellOrder = matchingQueue.pollSellOrder(); // 체결된 주문은 큐에서 제거해야 함
			if (sellOrder == null)
				break;
			executeMatch(order, sellOrder, marketPrice, matchingLog);
		}

		return matchingLog.toString();
	}

	// 가장 낮은 매도 주문의 가격을 시장 가격으로 설정
	private int determineMarketPrice() {
		Order bestSellOrder = matchingQueue.peekSellOrder();
		if (bestSellOrder == null) {
			throw new IllegalStateException("No available sell orders to determine market price.");
		}
		return bestSellOrder.getPrice();
	}

	// 주문 체결 로직
	private void executeMatch(Order marketOrder, Order sellOrder, int marketPrice, StringBuilder matchingLog) {
		int matchQuantity = Math.min(marketOrder.getRemainingQuantity(), sellOrder.getRemainingQuantity());
		marketOrder.reduceQuantity(matchQuantity);
		sellOrder.reduceQuantity(matchQuantity);

		saveTradeHistory(marketOrder, sellOrder, matchQuantity); // 거래 내역 저장
		logTradeExecution(marketOrder, sellOrder, matchQuantity, matchingLog); // 로그 기록

		updateOrderStatus(marketOrder);
		updateOrderStatus(sellOrder);

		orderRepository.save(marketOrder);
		orderRepository.save(sellOrder);
	}

	// 주문 상태 업데이트 (체결 완료 시 상태 변경)
	private void updateOrderStatus(Order order) {
		if (order.hasNoRemainingQuantity()) {
			order.complete();
			orderRepository.save(order);
		}
	}

	// 거래 내역 저장
	private void saveTradeHistory(Order marketOrder, Order sellOrder, int quantity) {
		TradeHistory tradeHistory = tradeHistoryRepository.save(
			TradeHistoryDTO.builder()
				.sellOrderId(sellOrder.getId())
				.buyOrderId(marketOrder.getId()) // 시장가 주문은 구매자가 됨
				.quantity(quantity)
				.price(sellOrder.getPrice()) // 체결된 가격 기록
				.build()
				.toEntity()
		);
        
		publisher.publishEvent(tradeHistory);
	}

	// 주문 접수 로그 기록
	private void logOrderReceived(Order order, StringBuilder log) {
		log.append(String.format("Market order received: %s\n", order));
	}

	// 체결 내역 로그 기록
	private void logTradeExecution(Order marketOrder, Order sellOrder, int quantity, StringBuilder log) {
		log.append(String.format("Market order executed: %d units at %d (Market: %d, Sell: %d)\n",
			quantity, sellOrder.getPrice(), marketOrder.getId(), sellOrder.getId()));
	}
}
