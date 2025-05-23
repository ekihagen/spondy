package no.spond.club.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

public class RegistrationRequestDto {
    
    @NotBlank(message = "Fullt navn er påkrevd")
    private String fullName;
    
    @NotBlank(message = "E-post er påkrevd")
    @Email(message = "E-post må ha gyldig format")
    private String email;
    
    @NotBlank(message = "Telefonnummer er påkrevd")
    private String phoneNumber;
    
    @NotNull(message = "Fødselsdato er påkrevd")
    @Past(message = "Fødselsdato må være i fortiden")
    private LocalDate birthDate;
    
    @NotBlank(message = "Medlemstype må velges")
    private String memberTypeId;
    
    // Constructors
    public RegistrationRequestDto() {}
    
    public RegistrationRequestDto(String fullName, String email, String phoneNumber, 
                                 LocalDate birthDate, String memberTypeId) {
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
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
    public String getMemberTypeId() {
        return memberTypeId;
    }
    
    public void setMemberTypeId(String memberTypeId) {
        this.memberTypeId = memberTypeId;
    }
} 