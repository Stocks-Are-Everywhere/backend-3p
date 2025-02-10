package org.scoula.three_people.member.repository;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl {

	private final AccountJpaRepository accountJpaRepository;

}
