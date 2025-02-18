package org.scoula.three_people.order.api.controller;

import org.scoula.three_people.order.service.TradeHistoryNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order-matching")
public class TradeHistoryNotificationController {

	private final TradeHistoryNotificationService tradeHistoryNotificationService;

	@GetMapping("/stream/{memberId}")
	public ResponseEntity<SseEmitter> subscribe(@PathVariable Long memberId) {
		return ResponseEntity.ok(tradeHistoryNotificationService.subscribe(memberId));
	}
}
