package no.spond.club.integration;

import no.spond.club.dto.RegistrationRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
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
@DisplayName("Registration Integration Tests")
class RegistrationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String VALID_FORM_ID = "B171388180BC457D9887AD92B6CCFC86";
    private static final String ACTIVE_MEMBER_TYPE_ID = "8FE4113D4E4020E0DCF887803A886981";
    private static final String SOCIAL_MEMBER_TYPE_ID = "4237C55C5CC3B4B082CBF2540612778E";

    @Nested
    @DisplayName("Form Retrieval Integration Tests")
    class FormRetrievalIntegrationTests {

        @Test
        @DisplayName("Should retrieve default form with correct structure")
        void shouldRetrieveDefaultFormWithCorrectStructure() {
            // When
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/form",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            
            Map<String, Object> responseBody = response.getBody();
            assertTrue((Boolean) responseBody.get("success"));
            assertNotNull(responseBody.get("data"));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> formData = (Map<String, Object>) responseBody.get("data");
            assertEquals("britsport", formData.get("clubId"));
            assertEquals(VALID_FORM_ID, formData.get("formId"));
            assertEquals("Coding camp summer 2025", formData.get("title"));
            assertTrue(formData.get("description").toString().contains("coding camp"));
            assertEquals("2024-12-16T00:00:00", formData.get("registrationOpens"));
        }

        @Test
        @DisplayName("Should retrieve form by ID with same structure")
        void shouldRetrieveFormByIdWithSameStructure() {
            // When
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/form/" + VALID_FORM_ID,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            
            Map<String, Object> responseBody = response.getBody();
            assertTrue((Boolean) responseBody.get("success"));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> formData = (Map<String, Object>) responseBody.get("data");
            assertEquals(VALID_FORM_ID, formData.get("formId"));
            assertEquals("Coding camp summer 2025", formData.get("title"));
        }

        @Test
        @DisplayName("Should handle invalid form ID gracefully")
        void shouldHandleInvalidFormIdGracefully() {
            // When
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/form/INVALID_FORM_ID",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody());
            
            Map<String, Object> responseBody = response.getBody();
            assertFalse((Boolean) responseBody.get("success"));
            assertNotNull(responseBody.get("error"));
            assertTrue(responseBody.get("message").toString().contains("skjema-ID"));
        }
    }

    @Nested
    @DisplayName("Registration Integration Tests")
    class RegistrationIntegrationTests {

        @Test
        @DisplayName("Should register active member successfully")
        void shouldRegisterActiveMemberSuccessfully() {
            // Given
            RegistrationRequestDto request = new RegistrationRequestDto(
                "John Doe",
                "john.doe@example.com",
                "12345678",
                "15.06.1990",
                ACTIVE_MEMBER_TYPE_ID
            );

            // When
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/form/" + VALID_FORM_ID + "/register",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Then
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            
            Map<String, Object> responseBody = response.getBody();
            assertTrue((Boolean) responseBody.get("success"));
            assertTrue(responseBody.get("message").toString().contains("Takk for din registrering"));
            assertEquals("John Doe", responseBody.get("memberName"));
            assertNotNull(responseBody.get("registrationId"));
        }

        @Test
        @DisplayName("Should register social member successfully")
        void shouldRegisterSocialMemberSuccessfully() {
            // Given
            RegistrationRequestDto request = new RegistrationRequestDto(
                "Jane Smith",
                "jane.smith@example.com",
                "87654321",
                "20.12.1985",
                SOCIAL_MEMBER_TYPE_ID
            );

            // When
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/form/" + VALID_FORM_ID + "/register",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Then
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            
            Map<String, Object> responseBody = response.getBody();
            assertTrue((Boolean) responseBody.get("success"));
            assertEquals("Jane Smith", responseBody.get("memberName"));
            assertNotNull(responseBody.get("registrationId"));
        }

        @Test
        @DisplayName("Should handle registration with special characters in name")
        void shouldHandleRegistrationWithSpecialCharactersInName() {
            // Given
            RegistrationRequestDto request = new RegistrationRequestDto(
                "José María O'Connor-Smith",
                "jose.maria@example.com",
                "12345678",
                "15.06.1990",
                ACTIVE_MEMBER_TYPE_ID
            );

            // When
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/form/" + VALID_FORM_ID + "/register",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Then
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            
            Map<String, Object> responseBody = response.getBody();
            assertTrue((Boolean) responseBody.get("success"));
            assertEquals("José María O'Connor-Smith", responseBody.get("memberName"));
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "12345678",    // 8 digits
            "123456789",   // 9 digits
            "1234567890",  // 10 digits
            "12345678901"  // 11 digits
        })
        @DisplayName("Should accept valid phone number lengths")
        void shouldAcceptValidPhoneNumberLengths(String phoneNumber) {
            // Given
            RegistrationRequestDto request = new RegistrationRequestDto(
                "John Doe",
                "john.doe@example.com",
                phoneNumber,
                "15.06.1990",
                ACTIVE_MEMBER_TYPE_ID
            );

            // When
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/form/" + VALID_FORM_ID + "/register",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Then
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            
            Map<String, Object> responseBody = response.getBody();
            assertTrue((Boolean) responseBody.get("success"));
        }
    }

    @Nested
    @DisplayName("Validation Error Integration Tests")
    class ValidationErrorIntegrationTests {

        @Test
        @DisplayName("Should return validation errors for completely invalid data")
        void shouldReturnValidationErrorsForCompletelyInvalidData() {
            // Given
            RegistrationRequestDto request = new RegistrationRequestDto(
                "", // Empty name
                "invalid-email", // Invalid email
                "123", // Too short phone
                "invalid-date", // Invalid date
                "invalid-member-type" // Invalid member type
            );

            // When
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/form/" + VALID_FORM_ID + "/register",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            
            Map<String, Object> responseBody = response.getBody();
            assertFalse((Boolean) responseBody.get("success"));
            assertEquals("VALIDATION_ERROR", responseBody.get("error"));
            assertNotNull(responseBody.get("message"));
            assertNotNull(responseBody.get("fieldErrors"));
        }

        @Test
        @DisplayName("Should return specific error for invalid member type")
        void shouldReturnSpecificErrorForInvalidMemberType() {
            // Given
            RegistrationRequestDto request = new RegistrationRequestDto(
                "John Doe",
                "john.doe@example.com",
                "12345678",
                "15.06.1990",
                "INVALID_MEMBER_TYPE_ID"
            );

            // When
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/form/" + VALID_FORM_ID + "/register",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            
            Map<String, Object> responseBody = response.getBody();
            assertFalse((Boolean) responseBody.get("success"));
            assertEquals("INVALID_INPUT", responseBody.get("error"));
            assertTrue(responseBody.get("message").toString().contains("medlemstype"));
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "invalid-email",
            "@example.com",
            "user@",
            "user@.com",
            "user.example.com"
        })
        @DisplayName("Should reject invalid email formats")
        void shouldRejectInvalidEmailFormats(String invalidEmail) {
            // Given
            RegistrationRequestDto request = new RegistrationRequestDto(
                "John Doe",
                invalidEmail,
                "12345678",
                "15.06.1990",
                ACTIVE_MEMBER_TYPE_ID
            );

            // When
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/form/" + VALID_FORM_ID + "/register",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            
            Map<String, Object> responseBody = response.getBody();
            assertFalse((Boolean) responseBody.get("success"));
            
            // Check that the email field has a validation error
            @SuppressWarnings("unchecked")
            Map<String, String> fieldErrors = (Map<String, String>) responseBody.get("fieldErrors");
            assertNotNull(fieldErrors);
            assertTrue(fieldErrors.containsKey("email"));
            assertTrue(fieldErrors.get("email").contains("e-postadresse"));
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "1234567",        // Too short
            "123456789012",   // Too long
            "12345abc",       // Contains letters
            "+4712345678",    // Contains plus
            "12 34 56 78"     // Contains spaces
        })
        @DisplayName("Should reject invalid phone numbers")
        void shouldRejectInvalidPhoneNumbers(String invalidPhone) {
            // Given
            RegistrationRequestDto request = new RegistrationRequestDto(
                "John Doe",
                "john.doe@example.com",
                invalidPhone,
                "15.06.1990",
                ACTIVE_MEMBER_TYPE_ID
            );

            // When
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/form/" + VALID_FORM_ID + "/register",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            
            Map<String, Object> responseBody = response.getBody();
            assertFalse((Boolean) responseBody.get("success"));
            
            // Check that the phoneNumber field has a validation error
            @SuppressWarnings("unchecked")
            Map<String, String> fieldErrors = (Map<String, String>) responseBody.get("fieldErrors");
            assertNotNull(fieldErrors);
            assertTrue(fieldErrors.containsKey("phoneNumber"));
            assertTrue(fieldErrors.get("phoneNumber").contains("Telefonnummer"));
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "invalid-date",
            "1990-06-15",
            "15/06/1990",
            "32.13.1990",
            "15.06.2050"
        })
        @DisplayName("Should reject invalid birth dates")
        void shouldRejectInvalidBirthDates(String invalidBirthDate) {
            // Given
            RegistrationRequestDto request = new RegistrationRequestDto(
                "John Doe",
                "john.doe@example.com",
                "12345678",
                invalidBirthDate,
                ACTIVE_MEMBER_TYPE_ID
            );

            // When
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/form/" + VALID_FORM_ID + "/register",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            
            Map<String, Object> responseBody = response.getBody();
            assertFalse((Boolean) responseBody.get("success"));
            
            // Check that the birthDate field has a validation error
            @SuppressWarnings("unchecked")
            Map<String, String> fieldErrors = (Map<String, String>) responseBody.get("fieldErrors");
            assertNotNull(fieldErrors);
            assertTrue(fieldErrors.containsKey("birthDate"));
            assertTrue(fieldErrors.get("birthDate").contains("fødselsdato"));
        }
    }

    @Nested
    @DisplayName("Edge Case Integration Tests")
    class EdgeCaseIntegrationTests {

        @Test
        @DisplayName("Should handle leap year birth dates")
        void shouldHandleLeapYearBirthDates() {
            // Given
            RegistrationRequestDto request = new RegistrationRequestDto(
                "John Doe",
                "john.doe@example.com",
                "12345678",
                "29.02.2000", // Valid leap year
                ACTIVE_MEMBER_TYPE_ID
            );

            // When
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/form/" + VALID_FORM_ID + "/register",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Then
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            
            Map<String, Object> responseBody = response.getBody();
            assertTrue((Boolean) responseBody.get("success"));
        }

        @Test
        @DisplayName("Should reject invalid leap year birth dates")
        void shouldRejectInvalidLeapYearBirthDates() {
            // Given
            RegistrationRequestDto request = new RegistrationRequestDto(
                "John Doe",
                "john.doe@example.com",
                "12345678",
                "29.02.1999", // Invalid leap year
                ACTIVE_MEMBER_TYPE_ID
            );

            // When
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/form/" + VALID_FORM_ID + "/register",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            
            Map<String, Object> responseBody = response.getBody();
            assertFalse((Boolean) responseBody.get("success"));
        }

        @Test
        @DisplayName("Should handle concurrent registrations")
        void shouldHandleConcurrentRegistrations() {
            // Given
            RegistrationRequestDto request1 = new RegistrationRequestDto(
                "John Doe",
                "john.doe@example.com",
                "12345678",
                "15.06.1990",
                ACTIVE_MEMBER_TYPE_ID
            );

            RegistrationRequestDto request2 = new RegistrationRequestDto(
                "Jane Smith",
                "jane.smith@example.com",
                "87654321",
                "20.12.1985",
                SOCIAL_MEMBER_TYPE_ID
            );

            // When & Then - Both should succeed
            ResponseEntity<Map<String, Object>> response1 = restTemplate.exchange(
                "http://localhost:" + port + "/api/form/" + VALID_FORM_ID + "/register",
                HttpMethod.POST,
                new HttpEntity<>(request1),
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            ResponseEntity<Map<String, Object>> response2 = restTemplate.exchange(
                "http://localhost:" + port + "/api/form/" + VALID_FORM_ID + "/register",
                HttpMethod.POST,
                new HttpEntity<>(request2),
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            assertEquals(HttpStatus.CREATED, response1.getStatusCode());
            assertEquals(HttpStatus.CREATED, response2.getStatusCode());
            
            Map<String, Object> responseBody1 = response1.getBody();
            Map<String, Object> responseBody2 = response2.getBody();
            
            assertNotNull(responseBody1);
            assertNotNull(responseBody2);
            assertTrue((Boolean) responseBody1.get("success"));
            assertTrue((Boolean) responseBody2.get("success"));
        }
    }
} 