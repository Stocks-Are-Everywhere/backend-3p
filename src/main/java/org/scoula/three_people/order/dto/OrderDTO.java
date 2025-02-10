package org.scoula.three_people.order.dto;

import lombok.Builder;
import lombok.Getter;
import org.scoula.three_people.order.controller.request.OrderRequest;
import org.scoula.three_people.order.domain.OrderStatus;
import org.scoula.three_people.order.domain.Type;

@Getter
@Builder
public class OrderDTO {
    private String companyCode;
    private Type type;
    private Integer totalQuantity;
    private Integer remainingQuantity;
    private OrderStatus status;
    private Integer price;
    private Long userId;

    public static OrderDTO fromRequest(OrderRequest request) {
        return OrderDTO.builder()
                .companyCode(request.getCompanyCode())
                .type(Type.valueOf(request.getType().toUpperCase()))
                .totalQuantity(request.getQuantity())
                .remainingQuantity(request.getQuantity())
                .status(OrderStatus.ACTIVE)
                .price(request.getPrice())
                .userId(request.getUserId())
                .build();
    }
}
