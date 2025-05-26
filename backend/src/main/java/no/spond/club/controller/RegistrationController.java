package no.spond.club.controller;

import jakarta.validation.Valid;
import no.spond.club.dto.RegistrationFormDto;
import no.spond.club.dto.RegistrationRequestDto;
import no.spond.club.service.RegistrationFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RegistrationController {
    
    private final RegistrationFormService registrationFormService;
    
    @Autowired
    public RegistrationController(RegistrationFormService registrationFormService) {
        this.registrationFormService = registrationFormService;
    }
    
    @GetMapping("/form")
    public ResponseEntity<Map<String, Object>> getDefaultForm() {
        try {
            RegistrationFormDto form = registrationFormService.getDefaultForm();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", form);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Kunne ikke hente registreringsskjema. Prøv igjen senere.");
            response.put("error", "FORM_FETCH_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/form/{id}")
    public ResponseEntity<Map<String, Object>> getFormById(@PathVariable Long id) {
        try {
            RegistrationFormDto form = registrationFormService.getFormById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", form);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Fant ikke registreringsskjema med ID: " + id);
            response.put("error", "FORM_NOT_FOUND");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    @PostMapping("/form/{formId}/register")
    public ResponseEntity<Map<String, Object>> registerMember(
            @PathVariable Long formId,
            @Valid @RequestBody RegistrationRequestDto request,
            BindingResult bindingResult) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Handle validation errors
        if (bindingResult.hasErrors()) {
            Map<String, String> fieldErrors = bindingResult.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage,
                    (existing, replacement) -> existing
                ));
            
            response.put("success", false);
            response.put("message", "Vennligst rett opp følgende feil:");
            response.put("error", "VALIDATION_ERROR");
            response.put("fieldErrors", fieldErrors);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        try {
            Long registrationId = registrationFormService.registerMember(formId, request);
            
            response.put("success", true);
            response.put("message", "Takk for din registrering! Du vil motta en bekreftelse på e-post.");
            response.put("registrationId", registrationId);
            response.put("memberName", request.getFullName());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("error", "INVALID_INPUT");
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "En uventet feil oppstod under registrering. Prøv igjen senere.");
            response.put("error", "REGISTRATION_ERROR");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "En uventet feil oppstod. Vennligst prøv igjen senere.");
        response.put("error", "INTERNAL_ERROR");
        
        // Log the actual error for debugging (in real app, use proper logging)
        System.err.println("Unhandled exception: " + e.getMessage());
        e.printStackTrace();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
} 