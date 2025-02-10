package org.scoula.three_people.order.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.three_people.order.controller.request.OrderRequest;
import org.scoula.three_people.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Void> placeOrder(@RequestBody OrderRequest orderRequest) {
        orderService.processOrder(orderRequest);
        return ResponseEntity.ok().build();
    }
}
