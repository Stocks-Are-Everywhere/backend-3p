package org.scoula.three_people.order.service.strategy;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.OrderHistory;
import org.scoula.three_people.order.dto.OrderHistoryDTO;
import org.scoula.three_people.order.repository.OrderHistoryRepositoryImpl;
import org.scoula.three_people.order.repository.OrderRepositoryImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchExecutor {

    private final OrderRepositoryImpl orderRepository;
    private final OrderHistoryRepositoryImpl orderHistoryRepository;
    private final ApplicationEventPublisher publisher;

    public void execute(Order buyer, Order seller, int matchQuantity, int matchedPrice, StringBuilder matchingLog) {
        buyer.reduceQuantity(matchQuantity);
        seller.reduceQuantity(matchQuantity);

        OrderHistory orderHistory = saveTradeHistory(buyer, seller, matchQuantity, matchedPrice);

        logTradeExecution(buyer, seller, matchQuantity, matchedPrice, matchingLog);

        updateOrderStatus(buyer);
        updateOrderStatus(seller);

        orderRepository.save(buyer);
        orderRepository.save(seller);

        publisher.publishEvent(orderHistory);
    }

    private OrderHistory saveTradeHistory(Order buyer, Order seller, int quantity, int price) {
        OrderHistory orderHistory = OrderHistoryDTO.builder()
                .sellOrderId(seller.getId())
                .buyOrderId(buyer.getId())
                .quantity(quantity)
                .price(price)
                .build()
                .toEntity();

        return orderHistoryRepository.save(orderHistory);
    }

    private void logTradeExecution(Order buyer, Order seller, int quantity, int price, StringBuilder log) {
        log.append(
                String.format("Trade executed: %d units at %d (Buy: %d, Sell: %d)\n",
                        quantity, price, buyer.getId(), seller.getId())
        );
    }

    private void updateOrderStatus(Order order) {
        if (order.hasNoRemainingQuantity()) {
            order.complete();
        }
    }
}
