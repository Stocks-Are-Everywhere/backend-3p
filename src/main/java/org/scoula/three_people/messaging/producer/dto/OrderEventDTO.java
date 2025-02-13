package org.scoula.three_people.messaging.producer.dto;

import java.time.LocalDateTime;

import org.scoula.three_people.order.domain.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderEventDTO {
    private Long sellOrderId;
    private Long buyOrderId;
    private Integer quantity;
    private Integer price;
    private LocalDateTime executedAt;
}
