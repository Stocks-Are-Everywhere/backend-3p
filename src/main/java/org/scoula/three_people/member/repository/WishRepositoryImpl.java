package org.scoula.three_people.member.repository;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WishRepositoryImpl {

	private final WishJpaRepository wishJpaRepository;

}
