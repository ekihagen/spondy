# API Documentation

Complete REST API documentation for the Spondy registration system.

## 🎯 Overview

The Spondy API provides endpoints for managing registration forms and member registrations. All endpoints return JSON responses with a consistent structure.

**Base URL**: `https://spondy.rotchess.com/api`

## 📋 Response Format

All API responses follow this consistent structure:

```json
{
  "success": boolean,
  "message": "Human-readable message",
  "data": object | null,
  "error": "ERROR_CODE" | null,
  "fieldErrors": object | null
}
```

### Success Response Example
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    "id": 123,
    "name": "Example"
  }
}
```

### Error Response Example
```json
{
  "success": false,
  "message": "Validation failed",
  "error": "VALIDATION_ERROR",
  "fieldErrors": {
    "email": "Invalid email format",
    "phoneNumber": "Phone number must be 8-11 digits"
  }
}
```

## 🔗 Endpoints

### 1. Get Default Registration Form

Retrieves the default registration form configuration.

**Endpoint**: `GET /api/form`

**Response**:
```json
{
  "success": true,
  "data": {
    "clubId": "britsport",
    "formId": "B171388180BC457D9887AD92B6CCFC86",
    "title": "Coding camp summer 2025",
    "description": "Join our exciting coding camp this summer! Learn programming, work on projects, and have fun with fellow developers.",
    "registrationOpens": "2024-12-16T00:00:00",
    "memberTypes": [
      {
        "id": "8FE4113D4E4020E0DCF887803A886981",
        "name": "Active Member"
      },
      {
        "id": "4237C55C5CC3B4B082CBF2540612778E",
        "name": "Social Member"
      }
    ]
  }
}
```

**Example Request**:
```bash
curl https://spondy.rotchess.com/api/form
```

### 2. Get Registration Form by ID

Retrieves a specific registration form by its ID.

**Endpoint**: `GET /api/form/{id}`

**Parameters**:
- `id` (path): Form ID (string)

**Response**: Same as default form endpoint

**Example Request**:
```bash
curl https://spondy.rotchess.com/api/form/B171388180BC457D9887AD92B6CCFC86
```

**Error Responses**:
- `404 Not Found`: Form not found
```json
{
  "success": false,
  "message": "Fant ikke registreringsskjema med ID: invalid-id",
  "error": "FORM_NOT_FOUND"
}
```

### 3. Register Member

Submits a new member registration.

**Endpoint**: `POST /api/form/{formId}/register`

**Parameters**:
- `formId` (path): Form ID (string)

**Request Body**:
```json
{
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "12345678",
  "birthDate": "15.06.1990",
  "memberTypeId": "8FE4113D4E4020E0DCF887803A886981"
}
```

**Field Validation**:
- `fullName`: Required, non-empty string
- `email`: Required, valid email format
- `phoneNumber`: Required, 8-11 digits only
- `birthDate`: Required, format DD.MM.YYYY, must be in the past
- `memberTypeId`: Required, must be valid member type ID

**Success Response**:
```json
{
  "success": true,
  "message": "Takk for din registrering! Du vil motta en bekreftelse på e-post.",
  "registrationId": 1703123456789,
  "memberName": "John Doe"
}
```

**Example Request**:
```bash
curl -X POST https://spondy.rotchess.com/api/form/B171388180BC457D9887AD92B6CCFC86/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "12345678",
    "birthDate": "15.06.1990",
    "memberTypeId": "8FE4113D4E4020E0DCF887803A886981"
  }'
```

**Error Responses**:

**Validation Error (400)**:
```json
{
  "success": false,
  "message": "Vennligst rett opp følgende feil:",
  "error": "VALIDATION_ERROR",
  "fieldErrors": {
    "email": "Ugyldig e-postadresse format",
    "phoneNumber": "Telefonnummer må være mellom 8-11 siffer og kun inneholde tall",
    "birthDate": "Ugyldig fødselsdato. Må være i format DD.MM.YYYY og i fortiden"
  }
}
```

**Invalid Input (400)**:
```json
{
  "success": false,
  "message": "Ugyldig medlemstype valgt. Vennligst velg en gyldig medlemstype.",
  "error": "INVALID_INPUT"
}
```

**Server Error (500)**:
```json
{
  "success": false,
  "message": "En uventet feil oppstod under registrering. Prøv igjen senere.",
  "error": "REGISTRATION_ERROR"
}
```

### 4. Health Check

Checks the health status of the backend service.

**Endpoint**: `GET /api/actuator/health`

**Response**:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 62725623808,
        "free": 31362811904,
        "threshold": 10485760,
        "path": "/."
      }
    }
  }
}
```

**Example Request**:
```bash
curl https://spondy.rotchess.com/api/actuator/health
```

## 📝 Data Models

### RegistrationFormDto

```typescript
interface RegistrationFormDto {
  clubId: string;
  formId: string;
  title: string;
  description: string;
  registrationOpens: string; // ISO 8601 datetime
  memberTypes: MemberTypeDto[];
}
```

### MemberTypeDto

```typescript
interface MemberTypeDto {
  id: string;
  name: string;
}
```

### RegistrationRequestDto

```typescript
interface RegistrationRequestDto {
  fullName: string;
  email: string;
  phoneNumber: string;
  birthDate: string; // Format: DD.MM.YYYY
  memberTypeId: string;
}
```

## 🔒 Security

### CORS Configuration

The API is configured to accept requests from:
- `https://spondy.rotchess.com`
- `http://spondy.rotchess.com`
- `http://localhost:5173` (development)
- `http://localhost:3000` (development)

### Rate Limiting

Rate limiting is implemented at the nginx level:
- **API endpoints**: 10 requests/second with burst of 20
- **Web endpoints**: 30 requests/second with burst of 50

### Input Validation

All input is validated both client-side and server-side:
- **Email**: RFC 5322 compliant format
- **Phone**: 8-11 digits, Norwegian format
- **Date**: DD.MM.YYYY format, must be in the past
- **Names**: Non-empty strings, reasonable length limits

## 🧪 Testing

### Manual Testing

**Test Registration Flow**:
```bash
# 1. Get form
curl https://spondy.rotchess.com/api/form | jq

# 2. Register member
curl -X POST https://spondy.rotchess.com/api/form/B171388180BC457D9887AD92B6CCFC86/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "email": "test@example.com",
    "phoneNumber": "12345678",
    "birthDate": "01.01.1990",
    "memberTypeId": "8FE4113D4E4020E0DCF887803A886981"
  }' | jq
```

**Test Validation**:
```bash
# Test with invalid data
curl -X POST https://spondy.rotchess.com/api/form/B171388180BC457D9887AD92B6CCFC86/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "",
    "email": "invalid-email",
    "phoneNumber": "123",
    "birthDate": "invalid-date",
    "memberTypeId": "invalid-type"
  }' | jq
```

### Automated Testing

The backend includes comprehensive test coverage:

```bash
# Run all tests
cd backend
./mvnw test

# Run specific test
./mvnw test -Dtest=RegistrationControllerTest
```

## 🐛 Error Codes

| Error Code | Description | HTTP Status |
|------------|-------------|-------------|
| `FORM_FETCH_ERROR` | Failed to retrieve form | 500 |
| `FORM_NOT_FOUND` | Form ID not found | 404 |
| `VALIDATION_ERROR` | Input validation failed | 400 |
| `INVALID_INPUT` | Business logic validation failed | 400 |
| `REGISTRATION_ERROR` | Registration processing failed | 500 |
| `INTERNAL_ERROR` | Unexpected server error | 500 |

## 📊 Response Times

**Target Performance**:
- `GET /api/form`: < 200ms
- `POST /api/form/{id}/register`: < 500ms
- `GET /api/actuator/health`: < 100ms

**Monitoring**: Response times are logged and can be monitored through application logs.

## 🔄 Versioning

**Current Version**: v1 (implicit)

**Future Versioning Strategy**:
- URL versioning: `/api/v2/form`
- Backward compatibility maintained for at least one major version
- Deprecation notices provided 6 months before removal

## 📞 Support

For API issues:
1. Check the error response for specific error codes
2. Verify request format matches documentation
3. Check network connectivity and CORS settings
4. Review application logs for detailed error information

**Development Environment**: Use `http://localhost:8080/api` for local testing. 