package org.scoula.three_people.order.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import org.scoula.three_people.order.domain.OrderHistory;

@Getter
@Builder
@AllArgsConstructor
public class OrderHistoryDTO {

	private Long sellOrderId;
	private Long buyOrderId;
	private Integer quantity;
	private Integer price;

	public OrderHistory toEntity() {
		return OrderHistory.builder()
				.sellOrderId(sellOrderId)
				.buyOrderId(buyOrderId)
				.quantity(quantity)
				.price(price)
				.build();
	}

}