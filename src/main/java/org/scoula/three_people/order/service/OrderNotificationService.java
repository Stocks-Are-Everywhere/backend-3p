package org.scoula.three_people.order.service;

import java.io.IOException;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.OrderHistory;
import org.scoula.three_people.order.dto.MatchingNotificationDTO;
import org.scoula.three_people.order.repository.OrderNotificationRepository;
import org.scoula.three_people.order.repository.OrderRepositoryImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderNotificationService {

	private static final Long NOTIFICATION_TIME_OUT = 60L * 60 * 60 * 60;

	private final OrderNotificationRepository orderNotificationRepository;
	private final OrderRepositoryImpl orderRepository;

	public SseEmitter subscribe(Long memberId) {
		SseEmitter emitter = new SseEmitter(NOTIFICATION_TIME_OUT);

		return orderNotificationRepository.save(memberId, emitter);
	}

	public void sendNotification(Long memberId, OrderHistory orderHistory) {
		SseEmitter emitter = orderNotificationRepository.findByMemberId(memberId);
		try {
			Order order = getMatchedOrder(orderHistory.getSellOrderId(), orderHistory.getBuyOrderId(), memberId);
			emitter.send(toMatchingNotificationDto(order, orderHistory));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private MatchingNotificationDTO toMatchingNotificationDto(Order order, OrderHistory orderHistory) {
		return MatchingNotificationDTO.builder()
			.orderId(order.getId())
			.companyCode(order.getCompanyCode())
			.type(order.getType())
			.price(orderHistory.getPrice())
			.quantity(orderHistory.getQuantity())
			.createdAt(orderHistory.getCreatedDateTime())
			.build();
	}

	private Order getMatchedOrder(Long sellOrderId, Long buyOrderId, Long memberId) {
		Order sellOrder = orderRepository.findById(sellOrderId)
			.orElseThrow(IllegalArgumentException::new);
		if (sellOrder.isSameMemberOrder(memberId)) {
			return sellOrder;
		}

		return orderRepository.findById(buyOrderId)
			.orElseThrow(IllegalArgumentException::new);
	}
}
