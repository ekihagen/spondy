# Development Guide

Complete guide for local development of the Spondy registration system.

## 🎯 Overview

Spondy is a full-stack registration system with:
- **Backend**: Spring Boot 3.x with Java 17
- **Frontend**: React 18 with TypeScript and Vite
- **Database**: PostgreSQL 15
- **Styling**: Tailwind CSS

## 🚀 Quick Start

### Prerequisites
- **Docker** and Docker Compose
- **Node.js 18+** and npm/yarn
- **Java 17+** and Maven
- **Git**

### 1. Clone and Setup
```bash
git clone <repository-url>
cd spondy
```

### 2. Choose Your Development Mode

We offer flexible development options depending on what you're working on:

#### Option A: Full Local Development (Recommended)
Best for active development on both frontend and backend.

```bash
# 1. Start only database in Docker
./dev-scripts/start-db-only.sh

# 2. Start backend locally (new terminal)
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# 3. Start frontend locally (new terminal)
cd frontend
npm install
npm run dev
```

**Access:**
- Frontend: http://localhost:5173 (Vite dev server)
- Backend: http://localhost:8080 (Spring Boot)
- Database: localhost:5432 (Docker)

#### Option B: Frontend Development
Backend and database in Docker, frontend local for fast iteration.

```bash
# 1. Start backend + database in Docker
./dev-scripts/start-backend-only.sh

# 2. Start frontend locally (new terminal)
cd frontend
npm install
npm run dev
```

**Access:**
- Frontend: http://localhost:5173 (local)
- Backend: http://localhost:8080 (Docker)

#### Option C: Full Docker
All services in Docker for testing production-like setup.

```bash
docker compose up -d --build
```

**Access:**
- Frontend: http://localhost:3000
- Backend: http://localhost:8080

### 3. Cleanup
```bash
# Stop and remove all development containers
./dev-scripts/cleanup.sh
```

## 🛠️ Development Workflows

### Backend Development

#### Project Structure
```
backend/
├── src/main/java/no/spond/club/
│   ├── controller/     # REST controllers
│   ├── service/        # Business logic
│   ├── dto/            # Data transfer objects
│   └── config/         # Configuration classes
├── src/main/resources/
│   ├── application.yml         # Main config
│   ├── application-dev.yml     # Development config
│   └── application-prod.yml    # Production config
└── src/test/           # Tests
```

#### Running Tests
```bash
cd backend

# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=RegistrationControllerTest

# Run with coverage
./mvnw test jacoco:report
```

#### Database Management
```bash
# Connect to development database
docker exec -it spondy-postgres-dev psql -U spondy_user -d spondy_db

# View tables
\dt

# View data
SELECT * FROM registration_forms;
```

#### API Testing
```bash
# Health check
curl http://localhost:8080/actuator/health

# Get registration form
curl http://localhost:8080/api/form

# Register member (POST)
curl -X POST http://localhost:8080/api/form/B171388180BC457D9887AD92B6CCFC86/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "email": "test@example.com",
    "phoneNumber": "12345678",
    "birthDate": "15.06.1990",
    "memberTypeId": "8FE4113D4E4020E0DCF887803A886981"
  }'
```

### Frontend Development

#### Project Structure
```
frontend/
├── src/
│   ├── components/     # React components
│   │   ├── ui/         # Reusable UI components
│   │   └── forms/      # Form-specific components
│   ├── pages/          # Page components
│   ├── hooks/          # Custom React hooks
│   ├── services/       # API service functions
│   ├── types/          # TypeScript type definitions
│   ├── utils/          # Utility functions
│   └── styles/         # Global styles
├── public/             # Static assets
└── tests/              # Test files
```

#### Development Commands
```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Run tests
npm run test

# Run tests in watch mode
npm run test:watch

# Type checking
npm run type-check

# Linting
npm run lint
npm run lint:fix
```

#### Component Development
```bash
# Create new component
mkdir src/components/MyComponent
touch src/components/MyComponent/MyComponent.tsx
touch src/components/MyComponent/index.ts
```

Example component structure:
```typescript
// src/components/MyComponent/MyComponent.tsx
import React from 'react';

interface MyComponentProps {
  title: string;
  onClick?: () => void;
}

export const MyComponent: React.FC<MyComponentProps> = ({ title, onClick }) => {
  return (
    <div className="p-4 bg-white rounded-lg shadow">
      <h2 className="text-xl font-semibold">{title}</h2>
      {onClick && (
        <button onClick={onClick} className="mt-2 px-4 py-2 bg-blue-500 text-white rounded">
          Click me
        </button>
      )}
    </div>
  );
};

// src/components/MyComponent/index.ts
export { MyComponent } from './MyComponent';
```

#### Styling with Tailwind
```typescript
// Use Tailwind classes for styling
<div className="max-w-md mx-auto bg-white rounded-xl shadow-md overflow-hidden">
  <div className="p-6">
    <h1 className="text-2xl font-bold text-gray-900">Title</h1>
    <p className="text-gray-600 mt-2">Description</p>
  </div>
</div>
```

## 🧪 Testing

### Backend Testing
```bash
cd backend

# Unit tests
./mvnw test

# Integration tests
./mvnw test -Dtest=*IntegrationTest

# Test with specific profile
./mvnw test -Dspring.profiles.active=test
```

### Frontend Testing
```bash
cd frontend

# Unit tests
npm run test

# Component tests
npm run test -- --testPathPattern=components

# E2E tests (if configured)
npm run test:e2e
```

### API Testing with curl
```bash
# Test registration flow
export API_BASE="http://localhost:8080/api"

# 1. Get form
curl "$API_BASE/form" | jq

# 2. Register member
curl -X POST "$API_BASE/form/B171388180BC457D9887AD92B6CCFC86/register" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "phoneNumber": "12345678",
    "birthDate": "01.01.1990",
    "memberTypeId": "8FE4113D4E4020E0DCF887803A886981"
  }' | jq
```

## 🔧 Configuration

### Environment Variables

#### Backend (application-dev.yml)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/spondy_db
    username: spondy_user
    password: spondy_pass
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  profiles:
    active: dev

logging:
  level:
    no.spond.club: DEBUG
    org.springframework.web: DEBUG
```

#### Frontend (.env.development)
```bash
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_TITLE=Spondy Registration (Dev)
```

### Database Configuration

#### Development Database
```bash
# Start development database
docker run -d \
  --name spondy-postgres-dev \
  -e POSTGRES_DB=spondy_db \
  -e POSTGRES_USER=spondy_user \
  -e POSTGRES_PASSWORD=spondy_pass \
  -p 5432:5432 \
  postgres:15-alpine
```

#### Database Migrations
```sql
-- Example migration (handled by Hibernate in dev)
CREATE TABLE IF NOT EXISTS registration_forms (
    id BIGSERIAL PRIMARY KEY,
    club_id VARCHAR(255) NOT NULL,
    form_id VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    registration_opens TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🐛 Debugging

### Backend Debugging
```bash
# Run with debug mode
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Connect debugger to port 5005
```

### Frontend Debugging
```bash
# Enable source maps in development (default in Vite)
# Use browser dev tools for debugging

# Debug API calls
console.log('API Response:', response);
```

### Common Issues

**1. Port conflicts**
```bash
# Check what's using port 8080
lsof -i :8080

# Kill process if needed
kill -9 <PID>
```

**2. Database connection issues**
```bash
# Check if PostgreSQL container is running
docker ps | grep postgres

# Check logs
docker logs spondy-postgres-dev
```

**3. CORS issues**
```bash
# Backend CORS is configured for development
# Check application-dev.yml for allowed origins
```

## 📦 Dependencies

### Backend Dependencies
```xml
<!-- Key dependencies in pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

### Frontend Dependencies
```json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "typescript": "^5.0.0",
    "tailwindcss": "^3.3.0",
    "lucide-react": "^0.263.1",
    "react-hook-form": "^7.45.0"
  },
  "devDependencies": {
    "@vitejs/plugin-react": "^4.0.0",
    "vite": "^4.4.0",
    "@types/react": "^18.2.0",
    "eslint": "^8.45.0",
    "prettier": "^3.0.0"
  }
}
```

## 🚀 Production Build

### Build for Production
```bash
# Backend
cd backend
./mvnw clean package -DskipTests

# Frontend
cd frontend
npm run build

# Docker images
docker build -t spondy-backend:prod ./backend
docker build -t spondy-frontend:prod ./frontend
```

### Local Production Testing
```bash
# Test production build locally
docker-compose -f docker-compose.prod.yml up -d

# Access at http://localhost
```

This development guide provides everything you need to contribute to the Spondy project effectively! 