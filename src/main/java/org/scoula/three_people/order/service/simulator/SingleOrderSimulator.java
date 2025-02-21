package org.scoula.three_people.order.service.simulator;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.scoula.three_people.order.api.dto.request.OrderRequest;
import org.scoula.three_people.order.domain.Type;
import org.scoula.three_people.order.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SingleOrderSimulator {
	private final OrderService orderService;
	private final Random random = new Random();
	private final ScheduledExecutorService executor;

	// 시뮬레이션 설정
	private static final BigDecimal BASE_PRICE = new BigDecimal("74000");
	private static final Integer PRICE_RANGE = 1000; // 기준가 ±1000원
	private static final Integer SECONDS_BETWEEN_ORDERS = 1;
	private static final Integer ORDER_INTERVAL = SECONDS_BETWEEN_ORDERS * 1000; // 밀리초 단위
	private static final List<String> COMPANY_CODES = Arrays.asList(
			// 실제 회사 코드 대신 예시로 "삼성전자" 사용
			"삼성전자"
	);

	public SingleOrderSimulator(final OrderService orderService) {
		this.orderService = orderService;
		this.executor = Executors.newSingleThreadScheduledExecutor();
	}

	// 시뮬레이션 시작
	public void startSimulation() {
		executor.scheduleAtFixedRate(
				this::generateRandomOrder,
				0,
				ORDER_INTERVAL,
				TimeUnit.MILLISECONDS
		);
	}

	// 랜덤 주문 생성 (시뮬레이션 주문)
	private void generateRandomOrder() {
		try {
			final OrderRequest orderRequest = createRandomOrder();
			// 시뮬레이션 주문 전용 메서드 호출
			orderService.placeSimulatedOrder(orderRequest);
		} catch (Exception e) {
			log.error("Error generating order", e);
		}
	}

	private OrderRequest createRandomOrder() {
		final String type = Type.values()[random.nextInt(Type.values().length)].name();
		final String companyCode = COMPANY_CODES.get(random.nextInt(COMPANY_CODES.size()));
		final Integer price = generateRandomPrice().intValue();
		final Integer quantity = generateRandomQuantity();
		final Long userId = (long)(random.nextInt(10000) + 1);

		return OrderRequest.builder()
				.companyCode(companyCode)
				.type(type)
				.price(price)
				.quantity(quantity)
				.userId(userId)
				.build();
	}

	private BigDecimal generateRandomPrice() {
		Integer priceOffset = random.nextInt(PRICE_RANGE * 2) - PRICE_RANGE;
		return BASE_PRICE.add(new BigDecimal(priceOffset));
	}

	private Integer generateRandomQuantity() {
		return random.nextInt(1000) + 100; // 100 ~ 1099
	}

	// 시뮬레이션 중지
	public void stopSimulation() {
		executor.shutdown();
	}
}
