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
    
    private static final List<String> ALLOWED_MEMBER_TYPES = Arrays.asList(
        "8FE4113D4E4020E0DCF887803A886981", // Active Member
        "4237C55C5CC3B4B082CBF2540612778E"  // Social Member
    );
    
    public RegistrationFormService() {
        // Constructor for service with hardcoded data
    }
    
    @Transactional(readOnly = true)
    public RegistrationFormDto getFormById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Ugyldig skjema-ID");
        }
        
        // Validate that the form ID is the expected one
        if (!"B171388180BC457D9887AD92B6CCFC86".equals(id)) {
            throw new IllegalArgumentException("Ugyldig skjema-ID");
        }
        
        return getDefaultForm(); // Return the form for the valid ID
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
    
    public Long registerMember(String formId, RegistrationRequestDto request) {
        // Validate form ID
        if (formId == null || formId.trim().isEmpty()) {
            throw new IllegalArgumentException("Ugyldig skjema-ID");
        }
        
        // Validate request object
        if (request == null) {
            throw new IllegalArgumentException("Registreringsdata mangler");
        }
        
        // Validate birth date format and that it's in the past
        if (!request.isValidBirthDate()) {
            throw new IllegalArgumentException("Ugyldig fødselsdato. Må være i format DD.MM.YYYY og i fortiden.");
        }
        
        // Validate phone number is numeric only and correct length
        String phoneNumber = request.getPhoneNumber();
        if (phoneNumber == null || !phoneNumber.matches("^\\d{8,11}$")) {
            throw new IllegalArgumentException("Telefonnummer må være mellom 8-11 siffer og kun inneholde tall.");
        }
        
        // Validate that memberTypeId is one of the allowed values
        if (!ALLOWED_MEMBER_TYPES.contains(request.getMemberTypeId())) {
            throw new IllegalArgumentException("Ugyldig medlemstype valgt. Vennligst velg en gyldig medlemstype.");
        }
        
        // Validate email format (additional check beyond annotation)
        String email = request.getEmail();
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Ugyldig e-postadresse format.");
        }
        
        // Validate full name is not just whitespace
        String fullName = request.getFullName();
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Fullt navn kan ikke være tomt.");
        }
        
        // Log the registration details for debugging (in real app, use proper logging)
        System.out.println("=== REGISTRERING MOTTATT ===");
        System.out.println("Skjema-ID: " + formId);
        System.out.println("Navn: " + request.getFullName());
        System.out.println("E-post: " + request.getEmail());
        System.out.println("Telefon: " + request.getPhoneNumber());
        System.out.println("Fødselsdato: " + request.getBirthDate());
        System.out.println("Medlemstype: " + request.getMemberTypeId());
        System.out.println("Tidspunkt: " + LocalDateTime.now());
        System.out.println("============================");
        
        // Simple registration logic - in real app would save to database
        // Generate a unique registration ID based on current time and hash
        long registrationId = System.currentTimeMillis() + request.hashCode();
        
        System.out.println("Registrering fullført med ID: " + registrationId);
        
        return registrationId;
    }
    
    /**
     * Helper method to get member type name by ID
     */
    public String getMemberTypeName(String memberTypeId) {
        if (memberTypeId == null) {
            return "Ukjent medlemstype";
        }
        
        switch (memberTypeId) {
            case "8FE4113D4E4020E0DCF887803A886981":
                return "Active Member";
            case "4237C55C5CC3B4B082CBF2540612778E":
                return "Social Member";
            default:
                return "Ukjent medlemstype";
        }
    }
}
 