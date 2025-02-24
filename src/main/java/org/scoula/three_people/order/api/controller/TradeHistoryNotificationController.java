package org.scoula.three_people.order.api.controller;

import org.scoula.three_people.order.api.controller.restApi.TradeHistoryApi;
import org.scoula.three_people.order.service.TradeHistoryNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class TradeHistoryNotificationController implements TradeHistoryApi {

	private final TradeHistoryNotificationService tradeHistoryNotificationService;

	@Override
	public ResponseEntity<SseEmitter> subscribe(Long memberId) {
		return ResponseEntity.ok(tradeHistoryNotificationService.subscribe(memberId));
	}
}
