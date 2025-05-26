package no.spond.club.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class RegistrationRequestDto {
    
    @NotBlank(message = "Fullt navn er påkrevd")
    private String fullName;
    
    @NotBlank(message = "E-post er påkrevd")
    @Email(message = "E-post må ha gyldig format")
    private String email;
    
    @NotBlank(message = "Telefonnummer er påkrevd")
    @Pattern(regexp = "^\\d{8,11}$", message = "Telefonnummer må være mellom 8-11 siffer")
    private String phoneNumber;
    
    @NotBlank(message = "Fødselsdato er påkrevd")
    @Pattern(regexp = "^\\d{2}\\.\\d{2}\\.\\d{4}$", message = "Fødselsdato må være i format DD.MM.YYYY")
    private String birthDate;
    
    @NotBlank(message = "Medlemstype må velges")
    private String memberTypeId;
    
    private static final DateTimeFormatter BIRTH_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    
    // Constructors
    public RegistrationRequestDto() {}
    
    public RegistrationRequestDto(String fullName, String email, String phoneNumber, 
                                 String birthDate, String memberTypeId) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.memberTypeId = memberTypeId;
    }
    
    // Getters and Setters
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
    
    public String getMemberTypeId() {
        return memberTypeId;
    }
    
    public void setMemberTypeId(String memberTypeId) {
        this.memberTypeId = memberTypeId;
    }
    
    // Helper method to parse birth date string to LocalDate
    public LocalDate getBirthDateAsLocalDate() throws DateTimeParseException {
        return LocalDate.parse(this.birthDate, BIRTH_DATE_FORMATTER);
    }
    
    // Validation method to check if birth date is in the past
    public boolean isValidBirthDate() {
        try {
            LocalDate parsedDate = getBirthDateAsLocalDate();
            return parsedDate.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }
} 