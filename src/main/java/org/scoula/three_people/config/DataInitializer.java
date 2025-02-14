package org.scoula.three_people.config;

import org.scoula.three_people.member.domain.Account;
import org.scoula.three_people.member.domain.Company;
import org.scoula.three_people.member.domain.Member;
import org.scoula.three_people.member.domain.MemberStatus;
import org.scoula.three_people.member.domain.Sector;
import org.scoula.three_people.member.domain.Wish;
import org.scoula.three_people.member.repository.AccountJpaRepository;
import org.scoula.three_people.member.repository.CompanyJpaRepository;
import org.scoula.three_people.member.repository.MemberJpaRepository;
import org.scoula.three_people.member.repository.WishJpaRepository;
import org.scoula.three_people.order.domain.Order;
import org.scoula.three_people.order.domain.OrderHistory;
import org.scoula.three_people.order.domain.OrderStatus;
import org.scoula.three_people.order.domain.Type;
import org.scoula.three_people.order.repository.OrderHistoryJpaRepository;
import org.scoula.three_people.order.repository.OrderJpaRepository;
import org.scoula.three_people.order.service.datastructure.OrderBook;
import org.scoula.three_people.order.service.datastructure.PriceTreeMap;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements ApplicationRunner {

	private final MemberJpaRepository memberRepository;
	private final AccountJpaRepository accountRepository;
	private final CompanyJpaRepository companyRepository;
	private final OrderJpaRepository orderRepository;
	private final OrderHistoryJpaRepository orderHistoryRepository;
	private final WishJpaRepository wishRepository;
	private final PriceTreeMap priceTreeMap;

	public DataInitializer(MemberJpaRepository memberRepository, AccountJpaRepository accountRepository,
		CompanyJpaRepository companyRepository, OrderJpaRepository orderRepository,
		OrderHistoryJpaRepository orderHistoryRepository, WishJpaRepository wishRepository,
						   PriceTreeMap priceTreeMap) {
		this.memberRepository = memberRepository;
		this.accountRepository = accountRepository;
		this.companyRepository = companyRepository;
		this.orderRepository = orderRepository;
		this.orderHistoryRepository = orderHistoryRepository;
		this.wishRepository = wishRepository;
		this.priceTreeMap = priceTreeMap;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) throws Exception {
		if (memberRepository.count() == 0) {
			loadDummyData();
		}
	}

	private void loadDummyData() {
		System.out.println("===== 더미 데이터 추가 시작 =====");

		Member member1 = memberRepository.save(Member.builder()
			.provider("GOOGLE")
			.email("user1@example.com")
			.nickname("User One")
			.status(MemberStatus.ACTIVE)
			.build());

		Member member2 = memberRepository.save(Member.builder()
			.provider("KAKAO")
			.email("user2@example.com")
			.nickname("User Two")
			.status(MemberStatus.ACTIVE)
			.build());

		Account account1 = accountRepository.save(Account.builder()
			.member(member1)
			.balance(100000L)
			.build());

		Account account2 = accountRepository.save(Account.builder()
			.member(member2)
			.balance(200000L)
			.build());

		Company company1 = companyRepository.save(Company.builder()
			.companyCode("COMP001")
			.sector(Sector.IT)
			.build());

		Company company2 = companyRepository.save(Company.builder()
			.companyCode("COMP002")
			.sector(Sector.FINANCE)
			.build());

		Order order1 = orderRepository.save(Order.builder()
			.companyCode("COMP001")
			.type(Type.BUY)
			.totalQuantity(10)
			.remainingQuantity(10)
			.status(OrderStatus.ACTIVE)
			.price(5000)
			.account(account1)
			.build());

		Order order2 = orderRepository.save(Order.builder()
			.companyCode("COMP002")
			.type(Type.SELL)
			.totalQuantity(5)
			.remainingQuantity(5)
			.status(OrderStatus.ACTIVE)
			.price(10000)
			.account(account2)
			.build());

		orderHistoryRepository.save(OrderHistory.builder()
			.sellOrderId(order2.getId())
			.buyOrderId(order1.getId())
			.quantity(5)
			.price(10000)
			.build());

		wishRepository.save(Wish.builder()
			.member(member1)
			.company(company1)
			.context("I want to invest in this company.")
			.build());

		priceTreeMap.addBuyOrder(order1);
		System.out.println("Added to MatchingQueue (BUY): " + order1);

		priceTreeMap.addSellOrder(order2);
		System.out.println("Added to MatchingQueue (SELL): " + order2);

		System.out.println("===== 더미 데이터 추가 완료 =====");
	}
}
