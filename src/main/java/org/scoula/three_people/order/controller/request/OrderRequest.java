package org.scoula.three_people.order.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
public class OrderRequest {
    private String companyCode;
    private String type;
    private Integer quantity;
    private Integer price;
    private Long userId;
}
