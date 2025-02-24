package org.scoula.three_people.order.websocket.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record OrderBookResponse(
        String companyCode,
        List<PriceLevelDto> sellLevels,
        List<PriceLevelDto> buyLevels
) {}
