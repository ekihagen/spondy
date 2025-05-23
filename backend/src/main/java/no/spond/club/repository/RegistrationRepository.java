package no.spond.club.repository;

import no.spond.club.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    
    List<Registration> findByFormId(Long formId);
    
    boolean existsByEmailAndFormId(String email, Long formId);
} 