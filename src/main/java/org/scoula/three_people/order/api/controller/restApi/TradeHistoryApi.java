package org.scoula.three_people.order.api.controller.restApi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

// @Tag(name = "Trade History API", description = "체결 내역 알림 관련 API")
@Tag(name = "Trade History")
public interface TradeHistoryApi {

    @GetMapping("/stream/{memberId}")
    @Operation(summary = "체결 내역 구독", description = "지정된 멤버의 체결 내역 알림을 SSE로 구독합니다.")
    ResponseEntity<SseEmitter> subscribe(@PathVariable Long memberId);
}
