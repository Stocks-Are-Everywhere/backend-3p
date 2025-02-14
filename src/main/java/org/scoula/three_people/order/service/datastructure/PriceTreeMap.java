package org.scoula.three_people.order.service.datastructure;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import org.scoula.three_people.order.domain.Order;
import org.springframework.stereotype.Component;

@Component
public class PriceTreeMap implements PriceMap {

	// ========== 지정가 주문을 위한 TreeMap ==========
	// 매수 주문: 가장 높은 가격 우선 (reverseOrder)
	private final TreeMap<Integer, PriceLevel> buyOrders =
			new TreeMap<>(Comparator.reverseOrder());

	// 매도 주문: 가장 낮은 가격 우선 (오름차순)
	private final TreeMap<Integer, PriceLevel> sellOrders =
			new TreeMap<>();

	// ========== 시장가 주문을 위한 Queue ==========
	private final Queue<Order> marketBuyOrders = new LinkedList<>();
	private final Queue<Order> marketSellOrders = new LinkedList<>();

	// ========== 마지막 체결가 ==========
	private int lastTradedPrice = 0;

	// ==================== 지정가: 등록 ====================
	@Override
	public void addBuyOrder(Order order) {
		// 시장가 주문이면 marketBuyOrders 로 보낸다
		if (order.isMarketOrder()) {
			addMarketBuyOrder(order);
			return;
		}
		// 지정가 주문이면 buyOrders 트리에 추가
		PriceLevel level = buyOrders.computeIfAbsent(order.getPrice(), k -> new PriceLevel());
		level.addOrder(order);
	}

	@Override
	public void addSellOrder(Order order) {
		// 시장가 주문이면 marketSellOrders 로 보낸다
		if (order.isMarketOrder()) {
			addMarketSellOrder(order);
			return;
		}
		// 지정가 주문이면 sellOrders 트리에 추가
		PriceLevel level = sellOrders.computeIfAbsent(order.getPrice(), k -> new PriceLevel());
		level.addOrder(order);
	}

	// ==================== 지정가: 포함 여부 ====================
	@Override
	public boolean containsBuyOrder(Order order) {
		if (order.isMarketOrder()) {
			return marketBuyOrders.stream()
					.anyMatch(o -> o.getId().equals(order.getId()));
		}
		PriceLevel level = buyOrders.get(order.getPrice());
		return (level != null && level.containsOrder(order));
	}

	@Override
	public boolean containsSellOrder(Order order) {
		if (order.isMarketOrder()) {
			return marketSellOrders.stream()
					.anyMatch(o -> o.getId().equals(order.getId()));
		}
		PriceLevel level = sellOrders.get(order.getPrice());
		return (level != null && level.containsOrder(order));
	}

	// ==================== 지정가: 최우선 호가 조회 / 추출 ====================
	@Override
	public Order peekBuyOrder() {
		// 시장가 주문이 존재하면 그것을 먼저 반환 (시장가 우선)
		if (!marketBuyOrders.isEmpty()) {
			return marketBuyOrders.peek();
		}
		// 그 외에는 TreeMap 중 가장 높은 가격(첫 엔트리)을 확인
		if (buyOrders.isEmpty()) {
			return null;
		}
		Map.Entry<Integer, PriceLevel> entry = buyOrders.firstEntry();
		return (entry == null) ? null : entry.getValue().peek();
	}

	@Override
	public Order pollBuyOrder() {
		// 시장가 먼저 확인
		if (!marketBuyOrders.isEmpty()) {
			return marketBuyOrders.poll();
		}
		if (buyOrders.isEmpty()) {
			return null;
		}
		Map.Entry<Integer, PriceLevel> entry = buyOrders.firstEntry();
		Order order = entry.getValue().poll();
		if (entry.getValue().isEmpty()) {
			buyOrders.remove(entry.getKey());
		}
		return order;
	}

	@Override
	public Order peekSellOrder() {
		// 시장가 먼저 확인
		if (!marketSellOrders.isEmpty()) {
			return marketSellOrders.peek();
		}
		if (sellOrders.isEmpty()) {
			return null;
		}
		Map.Entry<Integer, PriceLevel> entry = sellOrders.firstEntry();
		return (entry == null) ? null : entry.getValue().peek();
	}

	@Override
	public Order pollSellOrder() {
		// 시장가 먼저 확인
		if (!marketSellOrders.isEmpty()) {
			return marketSellOrders.poll();
		}
		if (sellOrders.isEmpty()) {
			return null;
		}
		Map.Entry<Integer, PriceLevel> entry = sellOrders.firstEntry();
		Order order = entry.getValue().poll();
		if (entry.getValue().isEmpty()) {
			sellOrders.remove(entry.getKey());
		}
		return order;
	}

	// ==================== 지정가: 존재 여부 ====================
	@Override
	public boolean hasBuyOrders() {
		// 시장가가 있으면 true
		if (!marketBuyOrders.isEmpty()) {
			return true;
		}
		// 트리에 있는지 확인
		return !buyOrders.isEmpty();
	}

	@Override
	public boolean hasSellOrders() {
		// 시장가가 있으면 true
		if (!marketSellOrders.isEmpty()) {
			return true;
		}
		return !sellOrders.isEmpty();
	}

	// ==================== 시장가: 추가 ====================
	@Override
	public void addMarketBuyOrder(Order order) {
		marketBuyOrders.offer(order);
	}

	@Override
	public void addMarketSellOrder(Order order) {
		marketSellOrders.offer(order);
	}

	// ==================== 시장가: 조회 / 추출 ====================
	@Override
	public Order peekMarketBuyOrder() {
		return marketBuyOrders.peek();
	}

	@Override
	public Order peekMarketSellOrder() {
		return marketSellOrders.peek();
	}

	@Override
	public Order pollMarketBuyOrder() {
		return marketBuyOrders.poll();
	}

	@Override
	public Order pollMarketSellOrder() {
		return marketSellOrders.poll();
	}

	// ==================== 시장가: 존재 여부 ====================
	@Override
	public boolean hasMarketBuyOrders() {
		return !marketBuyOrders.isEmpty();
	}

	@Override
	public boolean hasMarketSellOrders() {
		return !marketSellOrders.isEmpty();
	}

	// ==================== 마지막 체결가 ====================
	@Override
	public int getLastTradedPrice() {
		return lastTradedPrice;
	}

	@Override
	public void setLastTradedPrice(int price) {
		this.lastTradedPrice = price;
	}

	// ==================== 주문 취소 로직 예시 ====================
	@Override
	public void cancelOrder(Order order) {
		// 시장가 여부
		if (order.isMarketOrder()) {
			if (order.isBuyType()) {
				marketBuyOrders.removeIf(o -> o.getId().equals(order.getId()));
			} else {
				marketSellOrders.removeIf(o -> o.getId().equals(order.getId()));
			}
			return;
		}

		// 지정가
		if (order.isBuyType()) {
			PriceLevel level = buyOrders.get(order.getPrice());
			if (level != null) {
				// PriorityQueue에서 특정 오더를 제거하려면
				// 보통은 새로운 큐로 재구성하거나, removeIf(...) 로 처리
				// 여기서는 간단히 stream 필터링 등으로 구현 가능
				// 예시는 removeIf 사용:
				// level.removeOrder(order) 등의 별도 메서드를 구현할 수도 있음
				// 아래는 간단 예시:
				buyOrders.remove(order.getPrice());
			}
		} else {
			PriceLevel level = sellOrders.get(order.getPrice());
			if (level != null) {
				sellOrders.remove(order.getPrice());
			}
		}
	}
}
