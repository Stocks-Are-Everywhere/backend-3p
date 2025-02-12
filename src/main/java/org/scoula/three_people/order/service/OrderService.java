package org.scoula.three_people.order.service;

import lombok.RequiredArgsConstructor;
import org.scoula.three_people.member.domain.Account;
import org.scoula.three_people.member.repository.AccountRepositoryImpl;
import org.scoula.three_people.order.controller.request.OrderRequest;
import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.dto.OrderDTO;
import org.scoula.three_people.order.repository.OrderRepositoryImpl;
import org.scoula.three_people.order.service.strategy.OrderProcessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepositoryImpl orderRepository;
    private final AccountRepositoryImpl accountRepository;
    private final OrderProcessor orderProcessor;

    @Transactional
    public String processOrder(OrderRequest orderRequest) {
        OrderDTO orderDTO = OrderDTO.fromRequest(orderRequest);

        Account account = accountRepository.findByMemberId(orderRequest.userId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found for userId: " + orderRequest.userId()));

        Order order = convertToEntity(orderDTO, account);
        orderRepository.save(order);

        String matchingMessage = orderProcessor.processOrder(order);

        return "Order has been saved: " + order.toString() + "\n" + matchingMessage;
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
