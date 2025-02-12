package org.scoula.three_people.order.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class OrderNotificationRepository {

	private final Map<Long, SseEmitter> elements = new ConcurrentHashMap<>();

	public SseEmitter save(Long memberId, SseEmitter sseEmitter) {
		elements.put(memberId, sseEmitter);

		return elements.get(memberId);
	}

	public SseEmitter findByMemberId(Long memberId) {
		return elements.get(memberId);
	}
}
