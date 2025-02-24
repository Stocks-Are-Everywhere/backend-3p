package org.scoula.three_people.order.api.controller.restApi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.scoula.three_people.order.api.dto.request.OrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

// @Tag(name = "Order API", description = "주문 관련 API")
@Tag(name = "Order")
public interface OrderApi {

    @PostMapping
    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    ResponseEntity<Void> placeOrder(
            @RequestBody(
                    description = "주문 요청 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "orderRequest",
                                            value = "{\n  \"companyCode\": \"COMP001\",\n  \"type\": \"BUY\",\n  \"quantity\": 10,\n  \"price\": 5000,\n  \"userId\": 1\n}"
                                    )
                            }
                    )
            )
            OrderRequest orderRequest
    );

    @DeleteMapping("/{orderId}")
    @Operation(summary = "주문 취소", description = "주문을 취소합니다.")
    ResponseEntity<Map<String, String>> cancelOrder(@PathVariable Long orderId);
}
