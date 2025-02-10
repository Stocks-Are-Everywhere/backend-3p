package org.scoula.three_people.member.repository;

import org.scoula.three_people.member.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyJpaRepository extends JpaRepository<Company, Long> {
	
}
