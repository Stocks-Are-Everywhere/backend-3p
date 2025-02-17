package org.scoula.three_people.order.dto;


import lombok.Builder;

import org.scoula.three_people.order.domain.TradeHistory;

@Builder
public record TradeHistoryDTO(
		Long sellOrderId,
		Long buyOrderId,
		Integer quantity,
		Integer price
) {

	public TradeHistory toEntity() {
		return TradeHistory.builder()
				.sellOrderId(sellOrderId)
				.buyOrderId(buyOrderId)
				.quantity(quantity)
				.price(price)
				.build();
	}

}