package no.spond.club.service;

import no.spond.club.dto.RegistrationFormDto;
import no.spond.club.dto.RegistrationRequestDto;
import no.spond.club.dto.MemberTypeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RegistrationFormService Tests")
class RegistrationFormServiceTest {

    private RegistrationFormService registrationFormService;

    @BeforeEach
    void setUp() {
        registrationFormService = new RegistrationFormService();
    }

    @Nested
    @DisplayName("Form Retrieval Tests")
    class FormRetrievalTests {

        @Test
        @DisplayName("Should return default form successfully")
        void shouldReturnDefaultFormSuccessfully() {
            // When
            RegistrationFormDto form = registrationFormService.getDefaultForm();

            // Then
            assertNotNull(form);
            assertEquals("britsport", form.getClubId());
            assertEquals("B171388180BC457D9887AD92B6CCFC86", form.getFormId());
            assertEquals("Coding camp summer 2025", form.getTitle());
            assertNotNull(form.getDescription());
            assertTrue(form.getDescription().contains("coding camp"));
            assertEquals(LocalDateTime.of(2024, 12, 16, 0, 0, 0), form.getRegistrationOpens());
            
            List<MemberTypeDto> memberTypes = form.getMemberTypes();
            assertNotNull(memberTypes);
            assertEquals(2, memberTypes.size());
            
            // Verify member types
            assertTrue(memberTypes.stream().anyMatch(mt -> 
                "8FE4113D4E4020E0DCF887803A886981".equals(mt.getId()) && "Active Member".equals(mt.getName())));
            assertTrue(memberTypes.stream().anyMatch(mt -> 
                "4237C55C5CC3B4B082CBF2540612778E".equals(mt.getId()) && "Social Member".equals(mt.getName())));
        }

        @Test
        @DisplayName("Should return form by valid ID")
        void shouldReturnFormByValidId() {
            // Given
            String validId = "B171388180BC457D9887AD92B6CCFC86";

            // When
            RegistrationFormDto form = registrationFormService.getFormById(validId);

            // Then
            assertNotNull(form);
            assertEquals("Coding camp summer 2025", form.getTitle());
            assertEquals(2, form.getMemberTypes().size());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should throw exception for invalid form ID")
        void shouldThrowExceptionForInvalidFormId(String invalidId) {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> registrationFormService.getFormById(invalidId)
            );
            assertEquals("Ugyldig skjema-ID", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Member Registration Tests")
    class MemberRegistrationTests {

        @Test
        @DisplayName("Should register member successfully with valid data")
        void shouldRegisterMemberSuccessfully() {
            // Given
            String formId = "B171388180BC457D9887AD92B6CCFC86";
            RegistrationRequestDto request = new RegistrationRequestDto(
                "John Doe",
                "john.doe@example.com",
                "12345678",
                "15.06.1990",
                "8FE4113D4E4020E0DCF887803A886981"
            );

            // When
            Long registrationId = registrationFormService.registerMember(formId, request);

            // Then
            assertNotNull(registrationId);
            assertTrue(registrationId > 0);
        }

        @Test
        @DisplayName("Should register member with social member type")
        void shouldRegisterMemberWithSocialMemberType() {
            // Given
            String formId = "B171388180BC457D9887AD92B6CCFC86";
            RegistrationRequestDto request = new RegistrationRequestDto(
                "Jane Smith",
                "jane.smith@example.com",
                "87654321",
                "20.12.1985",
                "4237C55C5CC3B4B082CBF2540612778E" // Social Member
            );

            // When
            Long registrationId = registrationFormService.registerMember(formId, request);

            // Then
            assertNotNull(registrationId);
            assertTrue(registrationId > 0);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("Should throw exception for invalid form ID during registration")
        void shouldThrowExceptionForInvalidFormIdDuringRegistration(String invalidFormId) {
            // Given
            RegistrationRequestDto request = new RegistrationRequestDto(
                "John Doe",
                "john.doe@example.com",
                "12345678",
                "15.06.1990",
                "8FE4113D4E4020E0DCF887803A886981"
            );

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> registrationFormService.registerMember(invalidFormId, request)
            );
            assertEquals("Ugyldig skjema-ID", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for null registration request")
        void shouldThrowExceptionForNullRegistrationRequest() {
            // Given
            String formId = "B171388180BC457D9887AD92B6CCFC86";

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> registrationFormService.registerMember(formId, null)
            );
            assertEquals("Registreringsdata mangler", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        private String validFormId = "B171388180BC457D9887AD92B6CCFC86";

        @Nested
        @DisplayName("Birth Date Validation")
        class BirthDateValidationTests {

            @ParameterizedTest
            @ValueSource(strings = {
                "invalid-date",
                "1990-06-15",
                "15/06/1990",
                "15-06-1990",
                "32.13.1990",
                "00.00.0000",
                "15.06.2050" // Future date
            })
            @DisplayName("Should throw exception for invalid birth date formats")
            void shouldThrowExceptionForInvalidBirthDateFormats(String invalidBirthDate) {
                // Given
                RegistrationRequestDto request = new RegistrationRequestDto(
                    "John Doe",
                    "john.doe@example.com",
                    "12345678",
                    invalidBirthDate,
                    "8FE4113D4E4020E0DCF887803A886981"
                );

                // When & Then
                IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> registrationFormService.registerMember(validFormId, request)
                );
                assertTrue(exception.getMessage().contains("fødselsdato"));
            }

            @ParameterizedTest
            @ValueSource(strings = {
                "15.06.1990",
                "01.01.2000",
                "31.12.1985",
                "29.02.2000" // Leap year
            })
            @DisplayName("Should accept valid birth date formats")
            void shouldAcceptValidBirthDateFormats(String validBirthDate) {
                // Given
                RegistrationRequestDto request = new RegistrationRequestDto(
                    "John Doe",
                    "john.doe@example.com",
                    "12345678",
                    validBirthDate,
                    "8FE4113D4E4020E0DCF887803A886981"
                );

                // When & Then
                assertDoesNotThrow(() -> registrationFormService.registerMember(validFormId, request));
            }
        }

        @Nested
        @DisplayName("Phone Number Validation")
        class PhoneNumberValidationTests {

            @ParameterizedTest
            @ValueSource(strings = {
                "1234567", // Too short
                "123456789012", // Too long
                "12345abc",
                "abc12345",
                "+4712345678",
                "12 34 56 78",
                "12-34-56-78",
                ""
            })
            @DisplayName("Should throw exception for invalid phone numbers")
            void shouldThrowExceptionForInvalidPhoneNumbers(String invalidPhone) {
                // Given
                RegistrationRequestDto request = new RegistrationRequestDto(
                    "John Doe",
                    "john.doe@example.com",
                    invalidPhone,
                    "15.06.1990",
                    "8FE4113D4E4020E0DCF887803A886981"
                );

                // When & Then
                IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> registrationFormService.registerMember(validFormId, request)
                );
                assertTrue(exception.getMessage().contains("Telefonnummer"));
            }

            @Test
            @DisplayName("Should throw exception for null phone number")
            void shouldThrowExceptionForNullPhoneNumber() {
                // Given
                RegistrationRequestDto request = new RegistrationRequestDto(
                    "John Doe",
                    "john.doe@example.com",
                    null,
                    "15.06.1990",
                    "8FE4113D4E4020E0DCF887803A886981"
                );

                // When & Then
                IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> registrationFormService.registerMember(validFormId, request)
                );
                assertTrue(exception.getMessage().contains("Telefonnummer"));
            }

            @ParameterizedTest
            @ValueSource(strings = {
                "12345678", // 8 digits
                "123456789", // 9 digits
                "1234567890", // 10 digits
                "12345678901" // 11 digits
            })
            @DisplayName("Should accept valid phone numbers")
            void shouldAcceptValidPhoneNumbers(String validPhone) {
                // Given
                RegistrationRequestDto request = new RegistrationRequestDto(
                    "John Doe",
                    "john.doe@example.com",
                    validPhone,
                    "15.06.1990",
                    "8FE4113D4E4020E0DCF887803A886981"
                );

                // When & Then
                assertDoesNotThrow(() -> registrationFormService.registerMember(validFormId, request));
            }
        }

        @Nested
        @DisplayName("Email Validation")
        class EmailValidationTests {

            @ParameterizedTest
            @ValueSource(strings = {
                "invalid-email",
                "@example.com",
                "user@",
                "user@.com",
                "user@com",
                "user.example.com",
                "user@example.",
                ""
            })
            @DisplayName("Should throw exception for invalid email formats")
            void shouldThrowExceptionForInvalidEmailFormats(String invalidEmail) {
                // Given
                RegistrationRequestDto request = new RegistrationRequestDto(
                    "John Doe",
                    invalidEmail,
                    "12345678",
                    "15.06.1990",
                    "8FE4113D4E4020E0DCF887803A886981"
                );

                // When & Then
                IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> registrationFormService.registerMember(validFormId, request)
                );
                assertTrue(exception.getMessage().contains("e-postadresse"));
            }

            @Test
            @DisplayName("Should throw exception for null email")
            void shouldThrowExceptionForNullEmail() {
                // Given
                RegistrationRequestDto request = new RegistrationRequestDto(
                    "John Doe",
                    null,
                    "12345678",
                    "15.06.1990",
                    "8FE4113D4E4020E0DCF887803A886981"
                );

                // When & Then
                IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> registrationFormService.registerMember(validFormId, request)
                );
                assertTrue(exception.getMessage().contains("e-postadresse"));
            }

            @ParameterizedTest
            @ValueSource(strings = {
                "user@example.com",
                "test.email@domain.co.uk",
                "user+tag@example.org",
                "user_name@example-domain.com",
                "123@example.com"
            })
            @DisplayName("Should accept valid email formats")
            void shouldAcceptValidEmailFormats(String validEmail) {
                // Given
                RegistrationRequestDto request = new RegistrationRequestDto(
                    "John Doe",
                    validEmail,
                    "12345678",
                    "15.06.1990",
                    "8FE4113D4E4020E0DCF887803A886981"
                );

                // When & Then
                assertDoesNotThrow(() -> registrationFormService.registerMember(validFormId, request));
            }
        }

        @Nested
        @DisplayName("Full Name Validation")
        class FullNameValidationTests {

            @ParameterizedTest
            @NullAndEmptySource
            @ValueSource(strings = {"   ", "\t", "\n", "  \t  \n  "})
            @DisplayName("Should throw exception for invalid full names")
            void shouldThrowExceptionForInvalidFullNames(String invalidName) {
                // Given
                RegistrationRequestDto request = new RegistrationRequestDto(
                    invalidName,
                    "john.doe@example.com",
                    "12345678",
                    "15.06.1990",
                    "8FE4113D4E4020E0DCF887803A886981"
                );

                // When & Then
                IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> registrationFormService.registerMember(validFormId, request)
                );
                assertTrue(exception.getMessage().contains("navn"));
            }

            @ParameterizedTest
            @ValueSource(strings = {
                "John Doe",
                "Jane Smith-Johnson",
                "María García",
                "李小明",
                "O'Connor",
                "Jean-Pierre Dupont"
            })
            @DisplayName("Should accept valid full names")
            void shouldAcceptValidFullNames(String validName) {
                // Given
                RegistrationRequestDto request = new RegistrationRequestDto(
                    validName,
                    "john.doe@example.com",
                    "12345678",
                    "15.06.1990",
                    "8FE4113D4E4020E0DCF887803A886981"
                );

                // When & Then
                assertDoesNotThrow(() -> registrationFormService.registerMember(validFormId, request));
            }
        }

        @Nested
        @DisplayName("Member Type Validation")
        class MemberTypeValidationTests {

            @ParameterizedTest
            @ValueSource(strings = {
                "INVALID_ID",
                "123456789",
                "wrong-member-type",
                "",
                "8FE4113D4E4020E0DCF887803A88698", // Missing last character
                "8fe4113d4e4020e0dcf887803a886981" // Wrong case
            })
            @DisplayName("Should throw exception for invalid member type IDs")
            void shouldThrowExceptionForInvalidMemberTypeIds(String invalidMemberTypeId) {
                // Given
                RegistrationRequestDto request = new RegistrationRequestDto(
                    "John Doe",
                    "john.doe@example.com",
                    "12345678",
                    "15.06.1990",
                    invalidMemberTypeId
                );

                // When & Then
                IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> registrationFormService.registerMember(validFormId, request)
                );
                assertTrue(exception.getMessage().contains("medlemstype"));
            }

            @ParameterizedTest
            @ValueSource(strings = {
                "8FE4113D4E4020E0DCF887803A886981", // Active Member
                "4237C55C5CC3B4B082CBF2540612778E"  // Social Member
            })
            @DisplayName("Should accept valid member type IDs")
            void shouldAcceptValidMemberTypeIds(String validMemberTypeId) {
                // Given
                RegistrationRequestDto request = new RegistrationRequestDto(
                    "John Doe",
                    "john.doe@example.com",
                    "12345678",
                    "15.06.1990",
                    validMemberTypeId
                );

                // When & Then
                assertDoesNotThrow(() -> registrationFormService.registerMember(validFormId, request));
            }
        }
    }

    @Nested
    @DisplayName("Member Type Name Tests")
    class MemberTypeNameTests {

        @Test
        @DisplayName("Should return correct name for Active Member")
        void shouldReturnCorrectNameForActiveMember() {
            // When
            String name = registrationFormService.getMemberTypeName("8FE4113D4E4020E0DCF887803A886981");

            // Then
            assertEquals("Active Member", name);
        }

        @Test
        @DisplayName("Should return correct name for Social Member")
        void shouldReturnCorrectNameForSocialMember() {
            // When
            String name = registrationFormService.getMemberTypeName("4237C55C5CC3B4B082CBF2540612778E");

            // Then
            assertEquals("Social Member", name);
        }

        @ParameterizedTest
        @ValueSource(strings = {"INVALID_ID", "123", "", "wrong-id"})
        @DisplayName("Should return unknown member type for invalid IDs")
        void shouldReturnUnknownMemberTypeForInvalidIds(String invalidId) {
            // When
            String name = registrationFormService.getMemberTypeName(invalidId);

            // Then
            assertEquals("Ukjent medlemstype", name);
        }

        @Test
        @DisplayName("Should return unknown member type for null ID")
        void shouldReturnUnknownMemberTypeForNullId() {
            // When
            String name = registrationFormService.getMemberTypeName(null);

            // Then
            assertEquals("Ukjent medlemstype", name);
        }
    }
} 