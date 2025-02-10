package org.scoula.three_people.member.repository;

import org.scoula.three_people.member.domain.Account;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl {

	private final AccountJpaRepository accountJpaRepository;

	public Optional<Account> findById(Long id) {
		return accountJpaRepository.findById(id);
	}
}
