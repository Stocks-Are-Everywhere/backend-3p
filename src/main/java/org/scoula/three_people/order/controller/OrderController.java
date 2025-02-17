package org.scoula.three_people.order.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scoula.three_people.order.controller.request.OrderRequest;
import org.scoula.three_people.order.domain.TradeHistory;
import org.scoula.three_people.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	public ResponseEntity<List<TradeHistory>> placeOrder(@RequestBody OrderRequest orderRequest) {

		return ResponseEntity.ok(orderService.processOrder(orderRequest));
	}

	@DeleteMapping("/{orderId}")
	public ResponseEntity<Map<String, String>> cancelOrder(@PathVariable Long orderId) {
		String responseMessage = orderService.deleteOrder(orderId);

		Map<String, String> response = new HashMap<>();
		response.put("message", responseMessage);

		return ResponseEntity.ok(response);
	}
}
