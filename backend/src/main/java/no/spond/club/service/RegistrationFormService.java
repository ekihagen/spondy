package no.spond.club.service;

import no.spond.club.dto.*;
import no.spond.club.model.*;
import no.spond.club.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RegistrationFormService {
    
    private final RegistrationFormRepository formRepository;
    private final RegistrationRepository registrationRepository;
    private final MemberTypeRepository memberTypeRepository;
    private final GroupRepository groupRepository;
    
    @Autowired
    public RegistrationFormService(RegistrationFormRepository formRepository,
                                  RegistrationRepository registrationRepository,
                                  MemberTypeRepository memberTypeRepository,
                                  GroupRepository groupRepository) {
        this.formRepository = formRepository;
        this.registrationRepository = registrationRepository;
        this.memberTypeRepository = memberTypeRepository;
        this.groupRepository = groupRepository;
    }
    
    @Transactional(readOnly = true)
    public RegistrationFormDto getFormById(Long id) {
        RegistrationForm form = formRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Registreringsskjema ikke funnet"));
        
        return convertToDto(form);
    }
    
    @Transactional(readOnly = true)
    public RegistrationFormDto getDefaultForm() {
        RegistrationForm form = formRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("Ingen registreringsskjema funnet"));
        
        return convertToDto(form);
    }
    
    public Long registerMember(Long formId, RegistrationRequestDto request) {
        // Sjekk om skjemaet eksisterer
        RegistrationForm form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Registreringsskjema ikke funnet"));
        
        // Sjekk om bruker allerede er registrert
        if (registrationRepository.existsByEmailAndFormId(request.getEmail(), formId)) {
            throw new RuntimeException("En bruker med denne e-postadressen er allerede registrert");
        }
        
        // Finn medlemstype og gruppe
        MemberType memberType = memberTypeRepository.findById(request.getMemberTypeId())
                .orElseThrow(() -> new RuntimeException("Medlemstype ikke funnet"));
        
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Gruppe ikke funnet"));
        
        // Opprett registrering
        Registration registration = new Registration(
                request.getFullName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getBirthDate()
        );
        
        registration.setForm(form);
        registration.setMemberType(memberType);
        registration.setGroup(group);
        
        Registration saved = registrationRepository.save(registration);
        return saved.getId();
    }
    
    private RegistrationFormDto convertToDto(RegistrationForm form) {
        List<MemberTypeDto> memberTypeDtos = form.getMemberTypes().stream()
                .map(mt -> new MemberTypeDto(mt.getId(), mt.getName(), mt.getDescription(), mt.getPrice()))
                .collect(Collectors.toList());
        
        List<GroupDto> groupDtos = form.getGroups().stream()
                .map(g -> new GroupDto(g.getId(), g.getName(), g.getDescription()))
                .collect(Collectors.toList());
        
        return new RegistrationFormDto(
                form.getId(),
                form.getTitle(),
                form.getDescription(),
                form.getRegistrationDate(),
                memberTypeDtos,
                groupDtos
        );
    }
}
 