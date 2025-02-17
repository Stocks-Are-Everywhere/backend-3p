package org.scoula.three_people.order.service;

import java.io.IOException;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.TradeHistory;
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
	public void sendNotification(final TradeHistory tradeHistory) {
		try {
			sendNotificationToSellOrder(tradeHistory);
			sendNotificationToBuyOrder(tradeHistory);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private MatchingNotificationDTO toMatchingNotificationDto(final Order order, final TradeHistory tradeHistory) {
		return MatchingNotificationDTO.builder()
			.orderId(order.getId())
			.companyCode(order.getCompanyCode())
			.type(order.getType())
			.price(tradeHistory.getPrice())
			.quantity(tradeHistory.getQuantity())
			.createdAt(tradeHistory.getCreatedDateTime())
			.build();
	}

	private void sendNotificationToSellOrder(final TradeHistory tradeHistory) throws IOException {
		Order sellOrder = orderRepository.findById(tradeHistory.getSellOrderId()).orElseThrow();
		SseEmitter sellOrderEmitter = orderNotificationRepository.findByMemberId(
			sellOrder.getAccount().getMember().getId());

		if (sellOrderEmitter != null) {
			sellOrderEmitter.send(toMatchingNotificationDto(sellOrder, tradeHistory));
		} else {
			System.err.println("SellOrderEmitter is null for memberId: "
				+ sellOrder.getAccount().getMember().getId());
		}
	}

	private void sendNotificationToBuyOrder(final TradeHistory tradeHistory) throws IOException {
		Order buyOrder = orderRepository.findById(tradeHistory.getBuyOrderId()).orElseThrow();
		SseEmitter buyOrderEmitter = orderNotificationRepository.findByMemberId(
			buyOrder.getAccount().getMember().getId());

		if (buyOrderEmitter != null) {
			buyOrderEmitter.send(toMatchingNotificationDto(buyOrder, tradeHistory));
		} else {
			System.err.println("BuyOrderEmitter is null for memberId: "
				+ buyOrder.getAccount().getMember().getId());
		}
	}
}
