package org.scoula.three_people.order.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import org.scoula.three_people.order.domain.OrderHistory;

@Builder
public record OrderHistoryDTO(
		Long sellOrderId,
		Long buyOrderId,
		Integer quantity,
		Integer price
) {

	public OrderHistory toEntity() {
		return OrderHistory.builder()
				.sellOrderId(sellOrderId)
				.buyOrderId(buyOrderId)
				.quantity(quantity)
				.price(price)
				.build();
	}

}