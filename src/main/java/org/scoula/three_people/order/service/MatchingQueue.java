package org.scoula.three_people.order.service;

import org.scoula.three_people.order.domain.Order;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.PriorityQueue;

@Component
public class MatchingQueue {
    private final PriorityQueue<Order> buyOrders = new PriorityQueue<>(
            Comparator.comparing(Order::getPrice).reversed()
                    .thenComparing(Order::getCreatedDateTime)
    );

    private final PriorityQueue<Order> sellOrders = new PriorityQueue<>(
            Comparator.comparing(Order::getPrice)
                    .thenComparing(Order::getCreatedDateTime)
    );

    public void addBuyOrder(Order order) {
        buyOrders.add(order);
    }

    public void addSellOrder(Order order) {
        sellOrders.add(order);
    }

    public boolean containsBuyOrder(Order order) {
        return buyOrders.contains(order);
    }

    public boolean containsSellOrder(Order order) {
        return sellOrders.contains(order);
    }

    public Order peekBuyOrder() {
        return buyOrders.peek();
    }

    public Order peekSellOrder() {
        return sellOrders.peek();
    }

    public Order pollBuyOrder() {
        return buyOrders.poll();
    }

    public Order pollSellOrder() {
        return sellOrders.poll();
    }

    public boolean hasBuyOrders() {
        return !buyOrders.isEmpty();
    }

    public boolean hasSellOrders() {
        return !sellOrders.isEmpty();
    }
}
