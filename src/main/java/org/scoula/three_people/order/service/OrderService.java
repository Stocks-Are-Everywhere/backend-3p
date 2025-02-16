package org.scoula.three_people.order.service;

import java.util.List;

import org.scoula.three_people.member.domain.Account;
import org.scoula.three_people.member.repository.AccountRepositoryImpl;
import org.scoula.three_people.order.controller.request.OrderRequest;
import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.OrderHistory;
import org.scoula.three_people.order.dto.OrderDTO;
import org.scoula.three_people.order.repository.OrderHistoryRepositoryImpl;
import org.scoula.three_people.order.repository.OrderRepositoryImpl;
import org.scoula.three_people.order.service.datastructure.OrderBook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderService {

	private final OrderRepositoryImpl orderRepository;
	private final OrderHistoryRepositoryImpl orderHistoryRepository;
	private final AccountRepositoryImpl accountRepository;
	private final OrderBook orderBook;

	@Transactional
	public List<OrderHistory> processOrder(OrderRequest orderRequest) {
		OrderDTO orderDTO = OrderDTO.fromRequest(orderRequest);

		Account account = accountRepository.findByMemberId(orderRequest.userId())
			.orElseThrow(() -> new IllegalArgumentException("Account not found for userId: " + orderRequest.userId()));

		Order order = convertToEntity(orderDTO, account);
		orderRepository.save(order);
		return orderHistoryRepository.saveAllHistory(orderBook.matchFixedPrice(order));
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
