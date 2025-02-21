package org.scoula.three_people.order.service;

import java.util.List;

import org.scoula.three_people.member.domain.Account;
import org.scoula.three_people.member.repository.AccountRepositoryImpl;
import org.scoula.three_people.order.api.dto.request.OrderRequest;
import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.TradeHistory;
import org.scoula.three_people.order.dto.OrderDTO;
import org.scoula.three_people.order.repository.OrderRepositoryImpl;
import org.scoula.three_people.order.repository.TradeHistoryRepositoryImpl;
import org.scoula.three_people.order.websocket.dto.OrderBookResponse;
import org.scoula.three_people.order.websocket.handler.TradeWebSocketHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderService {

	private final OrderRepositoryImpl orderRepository;
	private final TradeHistoryRepositoryImpl tradeHistoryRepository;
	private final AccountRepositoryImpl accountRepository;
	private final OrderProcessor orderProcessor;
	private final TradeWebSocketHandler tradeWebSocketHandler;
	private final OrderBookService orderBookService;

	// 실제 주문: 체결(process) 로직 수행
	@Transactional
	public void placeOrder(OrderRequest orderRequest) {
		OrderDTO orderDTO = OrderDTO.fromRequest(orderRequest);

		Account account = accountRepository.findByMemberId(orderRequest.userId())
				.orElseThrow(() -> new IllegalArgumentException("Account not found for userId: " + orderRequest.userId()));

		Order order = convertToEntity(orderDTO, account);
		orderRepository.save(order);

		orderBookService.addOrder(order);

		OrderBookResponse orderBookResponse = orderBookService.getBook(order.getCompanyCode());
		tradeWebSocketHandler.broadcastOrderBook(orderBookResponse);

		// 실제 주문은 체결 로직 실행
		process(order);
	}

	// 시뮬레이션 주문: 체결(process) 로직 생략, 하지만 주문 Book 추가 및 소켓 브로드캐스트는 동일하게 수행
	@Transactional
	public void placeSimulatedOrder(OrderRequest orderRequest) {
		OrderDTO orderDTO = OrderDTO.fromRequest(orderRequest);

		Account account = accountRepository.findByMemberId(orderRequest.userId())
				.orElseThrow(() -> new IllegalArgumentException("Account not found for userId: " + orderRequest.userId()));

		Order order = convertToEntity(orderDTO, account);
		orderRepository.save(order);

		orderBookService.addOrder(order);

		OrderBookResponse orderBookResponse = orderBookService.getBook(order.getCompanyCode());
		tradeWebSocketHandler.broadcastOrderBook(orderBookResponse);

		// 시뮬레이션 주문은 체결 로직(process)이 생략됨
		System.out.println("Simulated order placed. Skipping trade processing.");
	}

	private void process(Order order) {
		List<TradeHistory> histories = orderProcessor.process(order);
		tradeHistoryRepository.saveAllHistory(histories);
	}

	@Transactional
	public String deleteOrder(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("Order not found for orderId: " + orderId));

		orderRepository.delete(order);

		return "Order has been deleted: " + order.toString();
	}

	private Order convertToEntity(OrderDTO dto, Account account) {
		return Order.builder()
				.companyCode(dto.companyCode())
				.type(dto.type())
				.totalQuantity(dto.totalQuantity())
				.remainingQuantity(dto.remainingQuantity())
				.status(dto.status())
				.price(dto.price())
				.account(account)
				.build();
	}
}
