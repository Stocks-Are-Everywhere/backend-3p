package org.scoula.three_people.order.domain;

import static jakarta.persistence.FetchType.*;

import org.scoula.three_people.global.entity.BaseEntity;
import org.scoula.three_people.member.domain.Account;
import org.scoula.three_people.order.constant.OrderConstant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Slf4j
@Table(name = "orders")
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long id;

	@Column(nullable = false)
	private String companyCode;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Type type;

	@Column(nullable = false)
	private Integer totalQuantity;

	@Column(nullable = false)
	private Integer remainingQuantity;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;

	@Column
	private Integer price;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;

	public void reduceQuantity(int quantity) {
		validateQuantity(quantity);
		remainingQuantity -= quantity;
	}

	public void complete() {
		this.status = OrderStatus.COMPLETE;
	}

	public boolean isComplete() {
		return this.status == OrderStatus.COMPLETE;
	}

	private void validateQuantity(int quantity) {
		if (quantity > remainingQuantity) {
			throw new IllegalArgumentException("Invalid quantity: " + quantity);
		}
	}

	public boolean hasNoRemainingQuantity() {
		return remainingQuantity == 0;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public boolean isBuyType() {
		return type == Type.BUY;
	}

	public boolean isMarketOrder() {
		return this.price == OrderConstant.MARKET_ORDER_PRICE.getValue();
	}
}
