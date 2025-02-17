package org.scoula.three_people.order.service.datastructure;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.OrderHistory;
import org.scoula.three_people.order.domain.OrderStatus;
import org.scoula.three_people.order.domain.Type;

class StockOrderBookTest {

	StockOrderBook stockOrderBook = new StockOrderBook();

	@Test
	void matchFixedPriceWithBuyOrder() {
		// given
		stockOrderBook.matchFixedPrice(generateOrder(Type.SELL, 2, 1L));
		stockOrderBook.matchFixedPrice(generateOrder(Type.SELL, 2, 2L));

		Order buy = generateOrder(Type.BUY, 4, 3L);

		// when
		List<OrderHistory> response = stockOrderBook.matchFixedPrice(buy);

		// then
		assertThat(response).hasSize(2);
	}

	@Test
	void matchFixedPricerWithSellOrder() {
		// given
		Order sell1 = generateOrder(Type.SELL, 2, 1L);
		Order sell2 = generateOrder(Type.SELL, 2, 2L);
		stockOrderBook.matchFixedPrice(sell1);
		stockOrderBook.matchFixedPrice(sell2);
		Order buy = generateOrder(Type.BUY, 4, 3L);

		// when
		List<OrderHistory> response = stockOrderBook.matchFixedPrice(buy);

		// then
		assertThat(response).hasSize(2);
	}

	@Test
	void matchFixedPricerWithSellOrd() {
		// given
		Order sell1 = generateOrder(Type.SELL, 2, 1L);
		Order sell2 = generateOrder(Type.SELL, 2, 2L);
		stockOrderBook.matchFixedPrice(sell1);
		stockOrderBook.matchFixedPrice(sell2);
		Order buy = generateOrder(Type.BUY, 2, 3L);

		// when
		List<OrderHistory> response = stockOrderBook.matchFixedPrice(buy);

		// then
		assertThat(response).hasSize(1);
	}

	@Test
	@DisplayName("존재하는 시장가 주문을 모두 소진한다.")
	void matchBuyOrderWithMarketOrderFirst() {
		// given
		stockOrderBook.matchFixedPrice(generateMarketOrder(Type.SELL, 2, 1L));
		stockOrderBook.matchFixedPrice(generateMarketOrder(Type.SELL, 2, 2L));
		Order buy = generateOrder(Type.BUY, 6, 4L);

		// when
		List<OrderHistory> marketOrder = stockOrderBook.matchWithMarketOrder(buy);

		// then
		assertThat(marketOrder).hasSize(2);
		marketOrder
			.forEach(orderHistory -> assertThat(orderHistory.getPrice()).isEqualTo(5000));
	}

	@Test
	@DisplayName("존재하는 시장가 주문을 모두 소진한다.")
	void matchSellOrderWithMarketOrderFirst() {
		// given
		stockOrderBook.matchFixedPrice(generateMarketOrder(Type.BUY, 2, 1L));
		stockOrderBook.matchFixedPrice(generateMarketOrder(Type.BUY, 2, 2L));
		Order buy = generateOrder(Type.SELL, 6, 4L);

		// when
		List<OrderHistory> marketOrder = stockOrderBook.matchWithMarketOrder(buy);

		// then
		assertThat(marketOrder).hasSize(2);
		marketOrder
			.forEach(orderHistory -> assertThat(orderHistory.getPrice()).isEqualTo(5000));
	}

	Order generateOrder(Type type, int quantity, Long id) {
		return Order.builder()
			.id(id)
			.companyCode("companyCode")
			.price(5000)
			.totalQuantity(quantity)
			.remainingQuantity(quantity)
			.status(OrderStatus.ACTIVE)
			.type(type)
			.createdDateTime(LocalDateTime.of(2025, 2, 16, 6, 21))
			.build();
	}

	Order generateMarketOrder(Type type, int quantity, Long id) {
		return Order.builder()
			.id(id)
			.companyCode("companyCode")
			.price(0)
			.totalQuantity(quantity)
			.remainingQuantity(quantity)
			.status(OrderStatus.ACTIVE)
			.type(type)
			.createdDateTime(LocalDateTime.of(2025, 2, 16, 6, 21))
			.build();
	}
}