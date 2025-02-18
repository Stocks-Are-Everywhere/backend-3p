package org.scoula.three_people.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import org.scoula.three_people.order.domain.OrderStatus;
import org.scoula.three_people.order.domain.TradeHistory;
import org.scoula.three_people.order.domain.Type;
import org.scoula.three_people.order.repository.OrderJpaRepository;
import org.scoula.three_people.order.repository.TradeHistoryJpaRepository;
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
	private final TradeHistoryJpaRepository tradeHistoryJpaRepository;
	private final WishJpaRepository wishRepository;
	private final PriceTreeMap priceTreeMap;

	public DataInitializer(MemberJpaRepository memberRepository, AccountJpaRepository accountRepository,
		CompanyJpaRepository companyRepository, OrderJpaRepository orderRepository,
		TradeHistoryJpaRepository tradeHistoryJpaRepository, WishJpaRepository wishRepository,
		PriceTreeMap priceTreeMap) {
		this.memberRepository = memberRepository;
		this.accountRepository = accountRepository;
		this.companyRepository = companyRepository;
		this.orderRepository = orderRepository;
		this.tradeHistoryJpaRepository = tradeHistoryJpaRepository;
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
		
		System.out.println("---- 더미 member, account  데이터 10,000개 추가 시작 ----");
		List<Member> members = new ArrayList<>();
		List<Account> accounts = new ArrayList<>();
		for (int i = 0; i < 10000; i++) {
			Member member = Member.builder()
				.provider("GOOGLE")
				.email("user" + i + "@example.com")
				.nickname("User " + i)
				.status(MemberStatus.ACTIVE)
				.build();
			members.add(member);
			accounts.add(Account.builder()
				.member(member)
				.balance(100000L)
				.build());
		}
		memberRepository.saveAll(members);
		accountRepository.saveAll(accounts);
		System.out.println("---- 더미 member, account 데이터 10,000개 추가 완료 ----");

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
			.account(accounts.get(0))
			.build());

		Order order2 = orderRepository.save(Order.builder()
			.companyCode("COMP002")
			.type(Type.SELL)
			.totalQuantity(5)
			.remainingQuantity(5)
			.status(OrderStatus.ACTIVE)
			.price(10000)
			.account(accounts.get(1))
			.build());

		tradeHistoryJpaRepository.save(TradeHistory.builder()
			.sellOrderId(order2.getId())
			.buyOrderId(order1.getId())
			.quantity(5)
			.price(10000)
			.tradeDateTime(LocalDateTime.of(2025, 2, 14, 14, 40))
			.build());

		wishRepository.save(Wish.builder()
			.member(members.get(0))
			.company(company1)
			.context("I want to invest in this company.")
			.build());

		priceTreeMap.matchWithSellOrder(order1);
		System.out.println("Added to MatchingQueue (BUY): " + order1);

		priceTreeMap.matchWithBuyOrder(order2);
		System.out.println("Added to MatchingQueue (SELL): " + order2);

		System.out.println("===== 더미 데이터 추가 완료 =====");
	}
}
