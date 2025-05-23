package no.spond.club.repository;

import no.spond.club.model.MemberType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberTypeRepository extends JpaRepository<MemberType, Long> {
} 