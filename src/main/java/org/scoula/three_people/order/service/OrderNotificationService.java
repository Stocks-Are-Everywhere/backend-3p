package org.scoula.three_people.order.service;

import org.scoula.three_people.order.repository.OrderNotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderNotificationService {

	private static final Long NOTIFICATION_TIME_OUT = 60L * 60 * 60 * 60;

	private final OrderNotificationRepository orderNotificationRepository;

	public SseEmitter subscribe(Long memberId) {
		SseEmitter emitter = new SseEmitter(NOTIFICATION_TIME_OUT);
		return orderNotificationRepository.save(memberId, emitter);
	}
}
