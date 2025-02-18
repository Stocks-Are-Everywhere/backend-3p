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

	@Transactional
	public void placeOrder(OrderRequest orderRequest) {
		OrderDTO orderDTO = OrderDTO.fromRequest(orderRequest);

		Account account = accountRepository.findByMemberId(orderRequest.userId())
			.orElseThrow(() -> new IllegalArgumentException("Account not found for userId: " + orderRequest.userId()));

		Order order = convertToEntity(orderDTO, account);
		orderRepository.save(order);

		process(order);
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
