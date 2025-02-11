package org.scoula.three_people.order.dto;

import lombok.Builder;
import org.scoula.three_people.order.controller.request.OrderRequest;
import org.scoula.three_people.order.domain.OrderStatus;
import org.scoula.three_people.order.domain.Type;

@Builder
public record OrderDTO(
        String companyCode,
        Type type,
        Integer totalQuantity,
        Integer remainingQuantity,
        OrderStatus status,
        Integer price,
        Long userId
) {

    public static OrderDTO fromRequest(OrderRequest request) {
        return OrderDTO.builder()
                .companyCode(request.companyCode())
                .type(Type.valueOf(request.type().toUpperCase()))
                .totalQuantity(request.quantity())
                .remainingQuantity(request.quantity())
                .status(OrderStatus.ACTIVE)
                .price(request.price())
                .userId(request.userId())
                .build();
    }
}
