package no.spond.club;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.spond.club.dto.RegistrationRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public class RegistrationControllerTest {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    public void shouldGetDefaultForm() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/form", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("title"));
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
        
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/form/1/register", 
                request, 
                String.class);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("success"));
    }
} 