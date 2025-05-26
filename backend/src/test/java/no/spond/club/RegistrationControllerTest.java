package no.spond.club;

import no.spond.club.dto.RegistrationRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public class RegistrationControllerTest {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    public void shouldGetDefaultFormSuccessfully() throws Exception {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/form",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {});
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertNotNull(responseBody.get("data"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> formData = (Map<String, Object>) responseBody.get("data");
        assertTrue(formData.containsKey("title"));
        assertTrue(formData.containsKey("memberTypes"));
        assertEquals("Coding camp summer 2025", formData.get("title"));
    }
    
    @Test
    public void shouldRegisterMemberSuccessfully() throws Exception {
        RegistrationRequestDto request = new RegistrationRequestDto(
                "Test Testesen",
                "test@example.com",
                "12345678",
                "15.06.1990",
                "8FE4113D4E4020E0DCF887803A886981" // Active Member
        );
        
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/form/B171388180BC457D9887AD92B6CCFC86/register",
                HttpMethod.POST,
                new org.springframework.http.HttpEntity<>(request),
                new ParameterizedTypeReference<Map<String, Object>>() {});
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertNotNull(responseBody.get("message"));
        assertNotNull(responseBody.get("registrationId"));
        assertEquals("Test Testesen", responseBody.get("memberName"));
        
        String message = (String) responseBody.get("message");
        assertTrue(message.contains("Takk for din registrering"));
    }
    
    @Test
    public void shouldReturnValidationErrorForInvalidData() throws Exception {
        RegistrationRequestDto request = new RegistrationRequestDto(
                "", // Empty name
                "invalid-email", // Invalid email
                "123", // Too short phone number
                "invalid-date", // Invalid date format
                "invalid-member-type" // Invalid member type
        );
        
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/form/B171388180BC457D9887AD92B6CCFC86/register",
                HttpMethod.POST,
                new org.springframework.http.HttpEntity<>(request),
                new ParameterizedTypeReference<Map<String, Object>>() {});
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertNotNull(responseBody.get("message"));
        assertEquals("VALIDATION_ERROR", responseBody.get("error"));
        
        // Should have field errors
        assertTrue(responseBody.containsKey("fieldErrors"));
    }
    
    @Test
    public void shouldReturnErrorForInvalidMemberType() throws Exception {
        RegistrationRequestDto request = new RegistrationRequestDto(
                "Test Testesen",
                "test@example.com",
                "12345678",
                "15.06.1990",
                "INVALID_MEMBER_TYPE_ID"
        );
        
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/form/B171388180BC457D9887AD92B6CCFC86/register",
                HttpMethod.POST,
                new org.springframework.http.HttpEntity<>(request),
                new ParameterizedTypeReference<Map<String, Object>>() {});
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals("INVALID_INPUT", responseBody.get("error"));
        
        String message = (String) responseBody.get("message");
        assertTrue(message.contains("medlemstype"));
    }
} 