package org.scoula.three_people.order.service.strategy;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.repository.OrderRepositoryImpl;
import org.scoula.three_people.order.repository.OrderHistoryRepositoryImpl;
import org.scoula.three_people.order.service.datastructure.PriceTreeMap;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class MarketOrderStrategy implements OrderStrategy {

	private final OrderRepositoryImpl orderRepository;
	private final OrderHistoryRepositoryImpl orderHistoryRepository;
	private final PriceTreeMap priceTreeMap;
	private final MatchExecutor matchExecutor;
	private final ApplicationEventPublisher publisher;

	@Transactional
	@Override
	public String process(Order order) {
		StringBuilder matchingLog = new StringBuilder();
		logOrderReceived(order, matchingLog);

		if (!priceTreeMap.hasSellOrders()) {
			throw new IllegalStateException("No available sell orders for market order.");
		}

		int marketPrice = determineMarketPrice();
		order.setPrice(marketPrice);

		while (priceTreeMap.hasSellOrders() && order.getRemainingQuantity() > 0) {
			Order sellOrder = priceTreeMap.peekSellOrder();
			if (sellOrder == null) {
				break;
			}

			executeMatch(order, sellOrder, marketPrice, matchingLog);

			if (sellOrder.hasNoRemainingQuantity()) {
				priceTreeMap.pollSellOrder();
			}
		}

		return matchingLog.toString();
	}


	private int determineMarketPrice() {
		Order bestSellOrder = priceTreeMap.peekSellOrder();
		if (bestSellOrder == null) {
			throw new IllegalStateException("No available sell orders to determine market price.");
		}
		return bestSellOrder.getPrice();
	}

	private void executeMatch(Order marketOrder, Order sellOrder, int marketPrice, StringBuilder matchingLog) {
		int matchQuantity = Math.min(marketOrder.getRemainingQuantity(), sellOrder.getRemainingQuantity());

		int matchedPrice = sellOrder.getPrice();

		matchExecutor.execute(marketOrder, sellOrder, matchQuantity, matchedPrice, matchingLog);
	}

	private void logOrderReceived(Order order, StringBuilder log) {
		log.append(String.format("Market order received: %s\n", order));
	}
}
