package no.spond.club.dto;

import java.time.LocalDate;
import java.util.List;

public class RegistrationFormDto {
    private Long id;
    private String title;
    private String description;
    private LocalDate registrationDate;
    private List<MemberTypeDto> memberTypes;
    private List<GroupDto> groups;
    
    // Constructors
    public RegistrationFormDto() {}
    
    public RegistrationFormDto(Long id, String title, String description, 
                              LocalDate registrationDate, List<MemberTypeDto> memberTypes, 
                              List<GroupDto> groups) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.registrationDate = registrationDate;
        this.memberTypes = memberTypes;
        this.groups = groups;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public List<MemberTypeDto> getMemberTypes() {
        return memberTypes;
    }
    
    public void setMemberTypes(List<MemberTypeDto> memberTypes) {
        this.memberTypes = memberTypes;
    }
    
    public List<GroupDto> getGroups() {
        return groups;
    }
    
    public void setGroups(List<GroupDto> groups) {
        this.groups = groups;
    }
} 