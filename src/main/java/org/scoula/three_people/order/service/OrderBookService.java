package org.scoula.three_people.order.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.websocket.dto.OrderBookResponse;
import org.scoula.three_people.order.websocket.dto.PriceLevelDto;
import org.springframework.stereotype.Service;

@Service
public class OrderBookService {

    private final Map<BigDecimal, Queue<Order>> sellOrders = new ConcurrentHashMap<>();
    private final Map<BigDecimal, Queue<Order>> buyOrders = new ConcurrentHashMap<>();

    public void addOrder(Order order) {
        if ("SELL".equalsIgnoreCase(order.getType().name())) {
            sellOrders.computeIfAbsent(new BigDecimal(order.getPrice()), k -> new ConcurrentLinkedQueue<>()).add(order);
        } else if ("BUY".equalsIgnoreCase(order.getType().name())) {
            buyOrders.computeIfAbsent(new BigDecimal(order.getPrice()), k -> new ConcurrentLinkedQueue<>()).add(order);
        }
    }

    public OrderBookResponse getBook(String companyCode) {
        List<PriceLevelDto> askLevels = sellOrders.entrySet().stream()
                .sorted(Map.Entry.<BigDecimal, Queue<Order>>comparingByKey().reversed())
                .limit(5)
                .map(entry -> PriceLevelDto.builder()
                        .price(entry.getKey())
                        .totalQuantity(calculateTotalQuantity(entry.getValue()))
                        .orderCount(entry.getValue().size())
                        .build())
                .collect(Collectors.toList());

        List<PriceLevelDto> bidLevels = buyOrders.entrySet().stream()
                .sorted(Map.Entry.<BigDecimal, Queue<Order>>comparingByKey().reversed())
                .limit(5)
                .map(entry -> PriceLevelDto.builder()
                        .price(entry.getKey())
                        .totalQuantity(calculateTotalQuantity(entry.getValue()))
                        .orderCount(entry.getValue().size())
                        .build())
                .collect(Collectors.toList());

        return OrderBookResponse.builder()
                .companyCode(companyCode)
                .sellLevels(askLevels)
                .buyLevels(bidLevels)
                .build();
    }

    private int calculateTotalQuantity(Queue<Order> orders) {
        return orders.stream().mapToInt(Order::getTotalQuantity).sum();
    }
}
