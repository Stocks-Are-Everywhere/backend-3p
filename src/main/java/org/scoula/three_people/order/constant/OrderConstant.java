package org.scoula.three_people.order.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderConstant {

	MARKET_ORDER_PRICE(0);

	private final int value;

	public int getValue() {
		return value;
	}
}
