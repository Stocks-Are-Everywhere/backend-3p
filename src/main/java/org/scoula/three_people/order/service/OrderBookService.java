package org.scoula.three_people.order.service;

import java.math.BigDecimal;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.websocket.dto.OrderBookResponse;
import org.scoula.three_people.order.websocket.dto.PriceLevelDto;
import org.springframework.stereotype.Service;

@Service
public class OrderBookService {

    private static final int MAX_CAPACITY = 10; // 최대 주문 수

    // 주문 생성 순서를 유지하는 Deque 사용 (동시성 고려)
    private final Deque<Order> sellOrders = new ConcurrentLinkedDeque<>();
    private final Deque<Order> buyOrders = new ConcurrentLinkedDeque<>();

    public void addOrder(Order order) {
        if ("SELL".equalsIgnoreCase(order.getType().name())) {
            if (sellOrders.size() >= MAX_CAPACITY) {
                sellOrders.pollFirst(); // 가장 오래된 주문 삭제
            }
            sellOrders.offerLast(order); // 새로운 주문 추가
        } else if ("BUY".equalsIgnoreCase(order.getType().name())) {
            if (buyOrders.size() >= MAX_CAPACITY) {
                buyOrders.pollFirst(); // 가장 오래된 주문 삭제
            }
            buyOrders.offerLast(order);
        }
    }

    public OrderBookResponse getBook(String companyCode) {
        // 매도 주문: 가격별로 그룹화 후 낮은 가격부터 정렬 (오름차순)
        Map<BigDecimal, List<Order>> groupedSellOrders = sellOrders.stream()
                .collect(Collectors.groupingBy(order -> new BigDecimal(order.getPrice())));

        List<PriceLevelDto> sellLevels = groupedSellOrders.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // 낮은 가격 순 정렬
                .limit(5)
                .map(entry -> PriceLevelDto.builder()
                        .price(entry.getKey())
                        .totalQuantity(entry.getValue().stream().mapToInt(Order::getTotalQuantity).sum())
                        .orderCount(entry.getValue().size())
                        .build())
                .collect(Collectors.toList());

        // 매수 주문: 가격별로 그룹화 후 높은 가격부터 정렬 (내림차순)
        Map<BigDecimal, List<Order>> groupedBuyOrders = buyOrders.stream()
                .collect(Collectors.groupingBy(order -> new BigDecimal(order.getPrice())));

        List<PriceLevelDto> buyLevels = groupedBuyOrders.entrySet().stream()
                .sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey()))
                .limit(5)
                .map(entry -> PriceLevelDto.builder()
                        .price(entry.getKey())
                        .totalQuantity(entry.getValue().stream().mapToInt(Order::getTotalQuantity).sum())
                        .orderCount(entry.getValue().size())
                        .build())
                .collect(Collectors.toList());

        return OrderBookResponse.builder()
                .companyCode(companyCode)
                .sellLevels(sellLevels)
                .buyLevels(buyLevels)
                .build();
    }

    private int calculateTotalQuantity(Queue<Order> orders) {
        return orders.stream().mapToInt(Order::getTotalQuantity).sum();
    }
}
