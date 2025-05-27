package no.spond.club.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class RegistrationRequestDto {
    
    @NotBlank(message = "Fullt navn er påkrevd")
    private String fullName;
    
    @NotBlank(message = "E-post er påkrevd")
    @Pattern(regexp = "^[A-Za-z0-9][A-Za-z0-9+_.-]*@[A-Za-z0-9][A-Za-z0-9.-]*\\.[A-Za-z]{2,}$", message = "Ugyldig e-postadresse format")
    private String email;
    
    @NotBlank(message = "Telefonnummer er påkrevd")
    @Pattern(regexp = "^\\d{8,11}$", message = "Telefonnummer må være mellom 8-11 siffer")
    private String phoneNumber;
    
    @NotBlank(message = "Fødselsdato er påkrevd")
    @ValidBirthDate(message = "Ugyldig fødselsdato. Må være i format DD.MM.YYYY og i fortiden")
    private String birthDate;
    
    @NotBlank(message = "Medlemstype må velges")
    private String memberTypeId;
    
    private static final DateTimeFormatter BIRTH_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    
    // Custom validation annotation for birth date
    @Documented
    @Constraint(validatedBy = BirthDateValidator.class)
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValidBirthDate {
        String message() default "Ugyldig fødselsdato";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }
    
    // Custom validator for birth date
    public static class BirthDateValidator implements ConstraintValidator<ValidBirthDate, String> {
        @Override
        public boolean isValid(String birthDate, ConstraintValidatorContext context) {
            if (birthDate == null || birthDate.trim().isEmpty()) {
                return false;
            }
            
            try {
                // First check if it matches the expected format
                if (!birthDate.matches("^\\d{2}\\.\\d{2}\\.\\d{4}$")) {
                    return false;
                }
                
                // Parse the components manually to validate them
                String[] parts = birthDate.split("\\.");
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);
                
                // Basic range checks
                if (day < 1 || day > 31 || month < 1 || month > 12 || year < 1900) {
                    return false;
                }
                
                // Check for impossible dates
                if (month == 2) { // February
                    boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
                    if (day > (isLeapYear ? 29 : 28)) {
                        return false;
                    }
                } else if (month == 4 || month == 6 || month == 9 || month == 11) { // April, June, September, November
                    if (day > 30) {
                        return false;
                    }
                }
                
                LocalDate parsedDate = LocalDate.parse(birthDate, BIRTH_DATE_FORMATTER);
                
                // Check if the date is in the future
                if (!parsedDate.isBefore(LocalDate.now())) {
                    return false;
                }
                
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }
    
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
    @JsonIgnore
    public LocalDate getBirthDateAsLocalDate() throws DateTimeParseException {
        if (birthDate == null || birthDate.trim().isEmpty()) {
            throw new DateTimeParseException("Birth date cannot be null or empty", birthDate, 0);
        }
        return LocalDate.parse(this.birthDate, BIRTH_DATE_FORMATTER);
    }
    
    // Validation method to check if birth date is valid and in the past
    public boolean isValidBirthDate() {
        if (birthDate == null || birthDate.trim().isEmpty()) {
            return false;
        }
        
        try {
            // First check if it matches the expected format
            if (!birthDate.matches("^\\d{2}\\.\\d{2}\\.\\d{4}$")) {
                return false;
            }
            
            // Parse the components manually to validate them
            String[] parts = birthDate.split("\\.");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            
            // Basic range checks
            if (day < 1 || day > 31 || month < 1 || month > 12 || year < 1900) {
                return false;
            }
            
            // Check for impossible dates
            if (month == 2) { // February
                boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
                if (day > (isLeapYear ? 29 : 28)) {
                    return false;
                }
            } else if (month == 4 || month == 6 || month == 9 || month == 11) { // April, June, September, November
                if (day > 30) {
                    return false;
                }
            }
            
            LocalDate parsedDate = getBirthDateAsLocalDate();
            
            // Check if the date is in the future
            if (!parsedDate.isBefore(LocalDate.now())) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistrationRequestDto that = (RegistrationRequestDto) o;
        return Objects.equals(fullName, that.fullName) &&
               Objects.equals(email, that.email) &&
               Objects.equals(phoneNumber, that.phoneNumber) &&
               Objects.equals(birthDate, that.birthDate) &&
               Objects.equals(memberTypeId, that.memberTypeId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(fullName, email, phoneNumber, birthDate, memberTypeId);
    }
    
    @Override
    public String toString() {
        return "RegistrationRequestDto{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", memberTypeId='" + memberTypeId + '\'' +
                '}';
    }
} 