package org.scoula.three_people.order.controller.request;

import lombok.Builder;
import lombok.Getter;

@Builder
public record OrderRequest(
        String companyCode,
        String type,
        Integer quantity,
        Integer price,
        Long userId
) {

}
