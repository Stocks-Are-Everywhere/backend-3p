package org.scoula.three_people.order.websocket.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TradeExecutionMessage {
    // @JsonProperty("companyCode")
    // private String companyCode;

    @JsonProperty("price")
    private int price;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("tradeDateTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime tradeDateTime;
}
