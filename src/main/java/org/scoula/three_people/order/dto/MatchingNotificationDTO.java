package org.scoula.three_people.order.dto;

import java.time.LocalDateTime;

import org.scoula.three_people.order.domain.Type;

import lombok.Builder;

@Builder
public record MatchingNotificationDTO(
	Long orderId,
	String companyCode,
	Type type,
	Integer price,
	Integer quantity,
	LocalDateTime createdAt
) {
}
