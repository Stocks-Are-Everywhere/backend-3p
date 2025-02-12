package org.scoula.three_people.order.service;

import java.io.IOException;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.OrderHistory;
import org.scoula.three_people.order.dto.MatchingNotificationDTO;
import org.scoula.three_people.order.repository.OrderNotificationRepository;
import org.scoula.three_people.order.repository.OrderRepositoryImpl;
import org.springframework.context.event.EventListener;
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

	@EventListener
	public void sendNotification(final OrderHistory orderHistory) {
		try {
			sendNotificationToSellOrder(orderHistory);
			sendNotificationToBuyOrder(orderHistory);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private MatchingNotificationDTO toMatchingNotificationDto(final Order order, final OrderHistory orderHistory) {
		return MatchingNotificationDTO.builder()
			.orderId(order.getId())
			.companyCode(order.getCompanyCode())
			.type(order.getType())
			.price(orderHistory.getPrice())
			.quantity(orderHistory.getQuantity())
			.createdAt(orderHistory.getCreatedDateTime())
			.build();
	}

	private void sendNotificationToSellOrder(final OrderHistory orderHistory) throws IOException {
		Order sellOrder = orderRepository.findById(orderHistory.getSellOrderId()).orElseThrow();
		SseEmitter sellOrderEmitter = orderNotificationRepository.findByMemberId(
			sellOrder.getAccount().getMember().getId());
		sellOrderEmitter.send(toMatchingNotificationDto(sellOrder, orderHistory));
	}

	private void sendNotificationToBuyOrder(final OrderHistory orderHistory) throws IOException {
		Order buyOrder = orderRepository.findById(orderHistory.getBuyOrderId()).orElseThrow();
		SseEmitter buyOrderEmitter = orderNotificationRepository.findByMemberId(
			buyOrder.getAccount().getMember().getId());
		buyOrderEmitter.send(toMatchingNotificationDto(buyOrder, orderHistory));
	}
}
