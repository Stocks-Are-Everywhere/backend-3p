package org.scoula.three_people.order.controller.request;

public record OrderHistoryRequest(
        String userId,
        String companyCode
) {

}
