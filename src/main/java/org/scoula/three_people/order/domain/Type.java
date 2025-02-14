package org.scoula.three_people.order.domain;

public enum Type {

	SELL,
	BUY;

	public boolean isDifferentType(Type otherType) {
		return this != otherType;
	}

	public boolean isBuyType() {
		return this == BUY;
	}
}
