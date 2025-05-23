package no.spond.club.dto;

import java.time.LocalDateTime;
import java.util.List;

public class RegistrationFormDto {
    private String clubId;
    private String formId;
    private String title;
    private String description;
    private LocalDateTime registrationOpens;
    private List<MemberTypeDto> memberTypes;
    
    // Constructors
    public RegistrationFormDto() {}
    
    public RegistrationFormDto(String clubId, String formId, String title, String description, 
                              LocalDateTime registrationOpens, List<MemberTypeDto> memberTypes) {
        this.clubId = clubId;
        this.formId = formId;
        this.title = title;
        this.description = description;
        this.registrationOpens = registrationOpens;
        this.memberTypes = memberTypes;
    }
    
    // Getters and Setters
    public String getClubId() {
        return clubId;
    }
    
    public void setClubId(String clubId) {
        this.clubId = clubId;
    }
    
    public String getFormId() {
        return formId;
    }
    
    public void setFormId(String formId) {
        this.formId = formId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getRegistrationOpens() {
        return registrationOpens;
    }
    
    public void setRegistrationOpens(LocalDateTime registrationOpens) {
        this.registrationOpens = registrationOpens;
    }
    
    public List<MemberTypeDto> getMemberTypes() {
        return memberTypes;
    }
    
    public void setMemberTypes(List<MemberTypeDto> memberTypes) {
        this.memberTypes = memberTypes;
    }
} 