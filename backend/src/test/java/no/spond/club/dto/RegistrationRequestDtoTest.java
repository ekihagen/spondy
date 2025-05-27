package no.spond.club.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RegistrationRequestDto Tests")
class RegistrationRequestDtoTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create DTO with valid data")
        void shouldCreateDtoWithValidData() {
            // Given
            String fullName = "John Doe";
            String email = "john.doe@example.com";
            String phoneNumber = "12345678";
            String birthDate = "15.06.1990";
            String memberTypeId = "8FE4113D4E4020E0DCF887803A886981";

            // When
            RegistrationRequestDto dto = new RegistrationRequestDto(
                fullName, email, phoneNumber, birthDate, memberTypeId
            );

            // Then
            assertEquals(fullName, dto.getFullName());
            assertEquals(email, dto.getEmail());
            assertEquals(phoneNumber, dto.getPhoneNumber());
            assertEquals(birthDate, dto.getBirthDate());
            assertEquals(memberTypeId, dto.getMemberTypeId());
        }

        @Test
        @DisplayName("Should create DTO with null values")
        void shouldCreateDtoWithNullValues() {
            // When
            RegistrationRequestDto dto = new RegistrationRequestDto(
                null, null, null, null, null
            );

            // Then
            assertNull(dto.getFullName());
            assertNull(dto.getEmail());
            assertNull(dto.getPhoneNumber());
            assertNull(dto.getBirthDate());
            assertNull(dto.getMemberTypeId());
        }
    }

    @Nested
    @DisplayName("Birth Date Validation Tests")
    class BirthDateValidationTests {

        @ParameterizedTest
        @ValueSource(strings = {
            "15.06.1990",
            "01.01.2000",
            "31.12.1985",
            "29.02.2000", // Leap year
            "28.02.1999", // Non-leap year
            "31.01.1980",
            "30.04.1995"
        })
        @DisplayName("Should validate correct birth date formats")
        void shouldValidateCorrectBirthDateFormats(String validBirthDate) {
            // Given
            RegistrationRequestDto dto = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", validBirthDate, "memberTypeId"
            );

            // When & Then
            assertTrue(dto.isValidBirthDate());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "invalid-date",
            "1990-06-15",
            "15/06/1990",
            "15-06-1990",
            "32.13.1990", // Invalid day
            "15.13.1990", // Invalid month
            "00.00.0000", // Invalid zeros
            "15.06.2050", // Future date
            "29.02.1999", // Invalid leap year
            "31.04.1990", // April doesn't have 31 days
            "31.02.1990", // February doesn't have 31 days
            "15.6.1990",  // Single digit month
            "5.06.1990",  // Single digit day
            "15.06.90",   // Two digit year
            ""
        })
        @DisplayName("Should reject invalid birth date formats")
        void shouldRejectInvalidBirthDateFormats(String invalidBirthDate) {
            // Given
            RegistrationRequestDto dto = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", invalidBirthDate, "memberTypeId"
            );

            // When & Then
            assertFalse(dto.isValidBirthDate());
        }

        @Test
        @DisplayName("Should reject null birth date")
        void shouldRejectNullBirthDate() {
            // Given
            RegistrationRequestDto dto = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", null, "memberTypeId"
            );

            // When & Then
            assertFalse(dto.isValidBirthDate());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should reject empty or whitespace birth dates")
        void shouldRejectEmptyOrWhitespaceBirthDates(String emptyBirthDate) {
            // Given
            RegistrationRequestDto dto = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", emptyBirthDate, "memberTypeId"
            );

            // When & Then
            assertFalse(dto.isValidBirthDate());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should be equal when all fields are the same")
        void shouldBeEqualWhenAllFieldsAreTheSame() {
            // Given
            RegistrationRequestDto dto1 = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", "15.06.1990", "memberTypeId"
            );
            RegistrationRequestDto dto2 = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", "15.06.1990", "memberTypeId"
            );

            // When & Then
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when fields differ")
        void shouldNotBeEqualWhenFieldsDiffer() {
            // Given
            RegistrationRequestDto dto1 = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", "15.06.1990", "memberTypeId"
            );
            RegistrationRequestDto dto2 = new RegistrationRequestDto(
                "Jane Doe", "john@example.com", "12345678", "15.06.1990", "memberTypeId"
            );

            // When & Then
            assertNotEquals(dto1, dto2);
        }

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            // Given
            RegistrationRequestDto dto = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", "15.06.1990", "memberTypeId"
            );

            // When & Then
            assertEquals(dto, dto);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            RegistrationRequestDto dto = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", "15.06.1990", "memberTypeId"
            );

            // When & Then
            assertNotEquals(dto, null);
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void shouldNotBeEqualToDifferentClass() {
            // Given
            RegistrationRequestDto dto = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", "15.06.1990", "memberTypeId"
            );

            // When & Then
            assertNotEquals(dto, "not a dto");
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should contain all field values in toString")
        void shouldContainAllFieldValuesInToString() {
            // Given
            RegistrationRequestDto dto = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", "15.06.1990", "memberTypeId"
            );

            // When
            String toString = dto.toString();

            // Then
            assertNotNull(toString);
            assertTrue(toString.contains("John Doe"));
            assertTrue(toString.contains("john@example.com"));
            assertTrue(toString.contains("12345678"));
            assertTrue(toString.contains("15.06.1990"));
            assertTrue(toString.contains("memberTypeId"));
        }

        @Test
        @DisplayName("Should handle null values in toString")
        void shouldHandleNullValuesInToString() {
            // Given
            RegistrationRequestDto dto = new RegistrationRequestDto(
                null, null, null, null, null
            );

            // When & Then
            assertDoesNotThrow(() -> dto.toString());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle very long strings")
        void shouldHandleVeryLongStrings() {
            // Given
            String longString = "a".repeat(1000);
            
            // When
            RegistrationRequestDto dto = new RegistrationRequestDto(
                longString, longString + "@example.com", "12345678", "15.06.1990", longString
            );

            // Then
            assertEquals(longString, dto.getFullName());
            assertEquals(longString + "@example.com", dto.getEmail());
            assertEquals(longString, dto.getMemberTypeId());
        }

        @Test
        @DisplayName("Should handle special characters in names")
        void shouldHandleSpecialCharactersInNames() {
            // Given
            String nameWithSpecialChars = "José María O'Connor-Smith";
            
            // When
            RegistrationRequestDto dto = new RegistrationRequestDto(
                nameWithSpecialChars, "jose@example.com", "12345678", "15.06.1990", "memberTypeId"
            );

            // Then
            assertEquals(nameWithSpecialChars, dto.getFullName());
        }

        @Test
        @DisplayName("Should handle Unicode characters")
        void shouldHandleUnicodeCharacters() {
            // Given
            String unicodeName = "李小明";
            String unicodeEmail = "李小明@example.com";
            
            // When
            RegistrationRequestDto dto = new RegistrationRequestDto(
                unicodeName, unicodeEmail, "12345678", "15.06.1990", "memberTypeId"
            );

            // Then
            assertEquals(unicodeName, dto.getFullName());
            assertEquals(unicodeEmail, dto.getEmail());
        }
    }

    @Nested
    @DisplayName("Boundary Value Tests")
    class BoundaryValueTests {

        @Test
        @DisplayName("Should handle minimum valid date")
        void shouldHandleMinimumValidDate() {
            // Given
            RegistrationRequestDto dto = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", "01.01.1900", "memberTypeId"
            );

            // When & Then
            assertTrue(dto.isValidBirthDate());
        }

        @Test
        @DisplayName("Should handle leap year edge cases")
        void shouldHandleLeapYearEdgeCases() {
            // Given & When & Then
            RegistrationRequestDto leapYear2000 = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", "29.02.2000", "memberTypeId"
            );
            assertTrue(leapYear2000.isValidBirthDate());

            RegistrationRequestDto leapYear2004 = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", "29.02.2004", "memberTypeId"
            );
            assertTrue(leapYear2004.isValidBirthDate());

            RegistrationRequestDto nonLeapYear1900 = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", "29.02.1900", "memberTypeId"
            );
            assertFalse(nonLeapYear1900.isValidBirthDate());

            RegistrationRequestDto nonLeapYear2001 = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", "29.02.2001", "memberTypeId"
            );
            assertFalse(nonLeapYear2001.isValidBirthDate());
        }

        @Test
        @DisplayName("Should handle month boundary values")
        void shouldHandleMonthBoundaryValues() {
            // Given & When & Then
            RegistrationRequestDto january = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", "31.01.1990", "memberTypeId"
            );
            assertTrue(january.isValidBirthDate());

            RegistrationRequestDto december = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", "31.12.1990", "memberTypeId"
            );
            assertTrue(december.isValidBirthDate());

            RegistrationRequestDto april = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", "30.04.1990", "memberTypeId"
            );
            assertTrue(april.isValidBirthDate());

            RegistrationRequestDto invalidApril = new RegistrationRequestDto(
                "John Doe", "john@example.com", "12345678", "31.04.1990", "memberTypeId"
            );
            assertFalse(invalidApril.isValidBirthDate());
        }
    }
} 