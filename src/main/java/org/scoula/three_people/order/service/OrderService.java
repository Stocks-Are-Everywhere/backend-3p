package org.scoula.three_people.order.service;

import lombok.RequiredArgsConstructor;
import org.scoula.three_people.member.domain.Account;
import org.scoula.three_people.member.repository.AccountRepositoryImpl;
import org.scoula.three_people.order.controller.request.OrderRequest;
import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.dto.OrderDTO;
import org.scoula.three_people.order.repository.OrderRepositoryImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepositoryImpl orderRepository;
    private final AccountRepositoryImpl accountRepository;
    private final OrderMatchingService orderMatchingService;

    @Transactional
    public String processOrder(OrderRequest orderRequest) {
        OrderDTO orderDTO = OrderDTO.fromRequest(orderRequest);

        Account account = accountRepository.findByMemberId(orderRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found for userId: " + orderRequest.getUserId()));

        Order order = convertToEntity(orderDTO, account);
        orderRepository.save(order);

        String matchingMessage = orderMatchingService.processOrder(order);

        return "Order has been saved: " + order.toString() + "\n" + matchingMessage;
    }


    private Order convertToEntity(OrderDTO dto, Account account) {
        return Order.builder()
                .companyCode(dto.getCompanyCode())
                .type(dto.getType())
                .totalQuantity(dto.getTotalQuantity())
                .remainingQuantity(dto.getRemainingQuantity())
                .status(dto.getStatus())
                .price(dto.getPrice())
                .account(account)
                .build();
    }
}
