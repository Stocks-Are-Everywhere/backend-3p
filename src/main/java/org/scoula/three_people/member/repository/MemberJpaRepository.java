package org.scoula.three_people.member.repository;

import org.scoula.three_people.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

}
