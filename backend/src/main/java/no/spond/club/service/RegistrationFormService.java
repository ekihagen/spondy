package no.spond.club.service;

import no.spond.club.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class RegistrationFormService {
    
    public RegistrationFormService() {
        // Constructor for service with hardcoded data
    }
    
    @Transactional(readOnly = true)
    public RegistrationFormDto getFormById(Long id) {
        return getDefaultForm(); // Return the same form for any ID
    }
    
    @Transactional(readOnly = true)
    public RegistrationFormDto getDefaultForm() {
        // Return data according to task specification (Appendix 1)
        List<MemberTypeDto> memberTypes = Arrays.asList(
            new MemberTypeDto("8FE4113D4E4020E0DCF887803A886981", "Active Member"),
            new MemberTypeDto("4237C55C5CC3B4B082CBF2540612778E", "Social Member")
        );
        
        return new RegistrationFormDto(
            "britsport",  // clubId
            "B171388180BC457D9887AD92B6CCFC86",  // formId
            "Coding camp summer 2025",  // title
            "Join our exciting coding camp this summer! Learn programming, work on projects, and have fun with fellow developers.",  // description
            LocalDateTime.of(2024, 12, 16, 0, 0, 0),  // registrationOpens
            memberTypes
        );
    }
    
    public Long registerMember(Long formId, RegistrationRequestDto request) {
        // Validate birth date format and that it's in the past
        if (!request.isValidBirthDate()) {
            throw new RuntimeException("Ugyldig fødselsdato. Må være i format DD.MM.YYYY og i fortiden.");
        }
        
        // Validate phone number is numeric only
        if (!request.getPhoneNumber().matches("^\\d{8,11}$")) {
            throw new RuntimeException("Telefonnummer må være mellom 8-11 siffer og kun inneholde tall.");
        }
        
        // Validate that memberTypeId is one of the allowed values
        List<String> allowedMemberTypes = Arrays.asList(
            "8FE4113D4E4020E0DCF887803A886981", // Active Member
            "4237C55C5CC3B4B082CBF2540612778E"  // Social Member
        );
        
        if (!allowedMemberTypes.contains(request.getMemberTypeId())) {
            throw new RuntimeException("Ugyldig medlemstype valgt.");
        }
        
        // Log the registration details for debugging
        System.out.println("Registrering mottatt:");
        System.out.println("Navn: " + request.getFullName());
        System.out.println("E-post: " + request.getEmail());
        System.out.println("Telefon: " + request.getPhoneNumber());
        System.out.println("Fødselsdato: " + request.getBirthDate());
        System.out.println("Medlemstype: " + request.getMemberTypeId());
        
        // Simple registration logic - in real app would save to database
        return System.currentTimeMillis(); // Return a unique registration ID
    }
}
 