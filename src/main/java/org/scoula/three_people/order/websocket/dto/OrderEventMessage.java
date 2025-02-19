package org.scoula.three_people.order.websocket.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderEventMessage {

    @JsonProperty("orderId")
    private Long orderId;

    @JsonProperty("companyCode")
    private String companyCode;

    @JsonProperty("type")
    private String type;

    @JsonProperty("price")
    private int price;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("status")
    private String status;
}
