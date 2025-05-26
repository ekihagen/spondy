package no.spond.club.controller;

import jakarta.validation.Valid;
import no.spond.club.dto.RegistrationFormDto;
import no.spond.club.dto.RegistrationRequestDto;
import no.spond.club.service.RegistrationFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RegistrationController {
    
    private final RegistrationFormService registrationFormService;
    
    @Autowired
    public RegistrationController(RegistrationFormService registrationFormService) {
        this.registrationFormService = registrationFormService;
    }
    
    @GetMapping("/form")
    public ResponseEntity<RegistrationFormDto> getDefaultForm() {
        try {
            RegistrationFormDto form = registrationFormService.getDefaultForm();
            return ResponseEntity.ok(form);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/form/{id}")
    public ResponseEntity<RegistrationFormDto> getFormById(@PathVariable Long id) {
        try {
            RegistrationFormDto form = registrationFormService.getFormById(id);
            return ResponseEntity.ok(form);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @PostMapping("/form/{formId}/register")
    public ResponseEntity<Map<String, Object>> registerMember(
            @PathVariable Long formId,
            @Valid @RequestBody RegistrationRequestDto request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long registrationId = registrationFormService.registerMember(formId, request);
            
            response.put("success", true);
            response.put("message", "Registrering fullført!");
            response.put("registrationId", registrationId);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "En feil oppstod: " + e.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
} 