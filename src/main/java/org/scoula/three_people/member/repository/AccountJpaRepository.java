package org.scoula.three_people.member.repository;

import org.scoula.three_people.member.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByMemberId(Long memberId);
}
