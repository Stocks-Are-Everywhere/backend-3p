package org.scoula.three_people.order.service;

import lombok.RequiredArgsConstructor;
import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.OrderHistory;
import org.scoula.three_people.order.domain.Type;
import org.scoula.three_people.order.dto.OrderHistoryDTO;
import org.scoula.three_people.order.repository.OrderHistoryRepositoryImpl;
import org.scoula.three_people.order.repository.OrderRepositoryImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

@RequiredArgsConstructor
@Service
public class OrderMatchingService {

    private final OrderRepositoryImpl orderRepository;
    private final OrderHistoryRepositoryImpl orderHistoryRepository;

    // 매수 주문 큐 (높은 가격 우선)
    private final Queue<Order> buyOrders = new PriorityQueue<>(
            Comparator.comparing(Order::getPrice).reversed()
                    .thenComparing(Order::getCreatedDateTime)
    );

    // 매도 주문 큐 (낮은 가격 우선)
    private final Queue<Order> sellOrders = new PriorityQueue<>(
            Comparator.comparing(Order::getPrice)
                    .thenComparing(Order::getCreatedDateTime)
    );

    @Transactional
    public String processOrder(Order order) {
        return (order.getType() == Type.BUY) ? matchBuyOrder(order) : matchSellOrder(order);
    }

    // 매수 주문을 매칭하여 체결하거나 큐에 추가
    private String matchBuyOrder(Order buyOrder) {
        StringBuilder message = new StringBuilder("Buy order added to queue: " + buyOrder + "\n");

        while (!sellOrders.isEmpty() && buyOrder.getRemainingQuantity() > 0) {
            Order sellOrder = sellOrders.peek();

            if (buyOrder.getPrice() < sellOrder.getPrice()) {
                break;
            }

            int tradeQuantity = Math.min(buyOrder.getRemainingQuantity(), sellOrder.getRemainingQuantity());
            buyOrder.reduceQuantity(tradeQuantity);
            sellOrder.reduceQuantity(tradeQuantity);

            saveTradeHistory(buyOrder, sellOrder, tradeQuantity);
            message.append(logTrade(buyOrder, sellOrder, tradeQuantity));

            if (sellOrder.hasNoRemainingQuantity()) {
                sellOrders.poll();
                sellOrder.complete();
            }
            orderRepository.save(sellOrder);

            if (buyOrder.hasNoRemainingQuantity()) {
                buyOrder.complete();
                orderRepository.save(buyOrder);
                return message.toString();
            }
        }

        buyOrders.add(buyOrder);
        return message.toString();
    }

    // 매도 주문을 매칭하여 체결하거나 큐에 추가
    private String matchSellOrder(Order sellOrder) {
        StringBuilder message = new StringBuilder("Sell order added to queue: " + sellOrder + "\n");

        while (!buyOrders.isEmpty() && sellOrder.getRemainingQuantity() > 0) {
            Order buyOrder = buyOrders.peek();

            if (buyOrder.getPrice() < sellOrder.getPrice()) {
                break;
            }

            int tradeQuantity = Math.min(buyOrder.getRemainingQuantity(), sellOrder.getRemainingQuantity());
            buyOrder.reduceQuantity(tradeQuantity);
            sellOrder.reduceQuantity(tradeQuantity);

            saveTradeHistory(buyOrder, sellOrder, tradeQuantity);
            message.append(logTrade(buyOrder, sellOrder, tradeQuantity));

            if (buyOrder.hasNoRemainingQuantity()) {
                buyOrders.poll();
                buyOrder.complete();
            }
            orderRepository.save(buyOrder);

            if (sellOrder.hasNoRemainingQuantity()) {
                sellOrder.complete();
                orderRepository.save(sellOrder);
                return message.toString();
            }
        }

        sellOrders.add(sellOrder);
        return message.toString();
    }

    // 체결된 거래 내역을 저장
    private void saveTradeHistory(Order buyOrder, Order sellOrder, int quantity) {
        OrderHistory orderHistory = OrderHistoryDTO.builder()
                .sellOrderId(sellOrder.getId())
                .buyOrderId(buyOrder.getId())
                .quantity(quantity)
                .price(sellOrder.getPrice())
                .build()
                .toEntity();

        orderHistoryRepository.save(orderHistory);
    }

    // 체결된 거래 내역을 로그에 기록
    private String logTrade(Order buyOrder, Order sellOrder, int quantity) {
        return String.format(
                "Trade executed: %d units at %d (BUY Account ID: %d, SELL Account ID: %d)\n",
                quantity, sellOrder.getPrice(),
                buyOrder.getAccount().getId(),
                sellOrder.getAccount().getId()
        );
    }
}
