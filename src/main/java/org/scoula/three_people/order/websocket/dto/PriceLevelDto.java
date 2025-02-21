package org.scoula.three_people.order.websocket.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PriceLevelDto {
    private BigDecimal price;
    private int totalQuantity;
    private int orderCount;
}
