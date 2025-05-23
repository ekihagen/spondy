package no.spond.club.repository;

import no.spond.club.model.RegistrationForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationFormRepository extends JpaRepository<RegistrationForm, Long> {
    
    @Query("SELECT f FROM RegistrationForm f " +
           "LEFT JOIN FETCH f.memberTypes " +
           "LEFT JOIN FETCH f.groups " +
           "WHERE f.id = :id")
    Optional<RegistrationForm> findByIdWithDetails(Long id);
    
    Optional<RegistrationForm> findFirstByOrderByIdAsc();
} 