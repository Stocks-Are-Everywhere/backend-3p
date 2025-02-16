package org.scoula.three_people.order.service.datastructure;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

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

	Order generateOrder(Type type, int quantity, Long id) {
		return Order.builder()
			.companyCode("companyCode")
			.price(5000)
			.totalQuantity(quantity)
			.remainingQuantity(quantity)
			.status(OrderStatus.ACTIVE)
			.type(type)
			.createdDateTime(LocalDateTime.of(2025, 2, 16, 6, 21))
			.build();
	}
}