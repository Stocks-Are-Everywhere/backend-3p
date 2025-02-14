package org.scoula.three_people.order.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.three_people.order.controller.request.OrderRequest;
import org.scoula.three_people.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Map<String, String>> placeOrder(@RequestBody OrderRequest orderRequest) {
        String responseMessage = orderService.processOrder(orderRequest);

        Map<String, String> response = new HashMap<>();
        response.put("message", responseMessage);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Map<String, String>> cancelOrder(@PathVariable Long orderId) {
        String responseMessage = orderService.deleteOrder(orderId);

        Map<String, String> response = new HashMap<>();
        response.put("message", responseMessage);

        return ResponseEntity.ok(response);
    }
}
