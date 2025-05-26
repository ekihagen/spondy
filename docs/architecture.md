# System Architecture

Technical architecture and design decisions for the Spondy registration system.

## 🏗️ Overview

Spondy is a modern full-stack web application built with industry-standard technologies and following best practices for security, scalability, and maintainability.

## 🎯 Architecture Principles

- **Separation of Concerns**: Clear separation between frontend, backend, and database
- **Security First**: Non-root containers, localhost binding, SSL/TLS termination
- **Scalability**: Containerized services with reverse proxy architecture
- **Maintainability**: Clean code structure, comprehensive documentation
- **Performance**: Optimized builds, caching, compression

## 🏛️ System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Internet                             │
└─────────────────────┬───────────────────────────────────────┘
                      │ HTTPS (443)
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                Raspberry Pi Server                         │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                Host Nginx                           │   │
│  │            (Reverse Proxy)                          │   │
│  │         SSL/TLS Termination                         │   │
│  │         Security Headers                            │   │
│  │         Gzip Compression                            │   │
│  └─────────────────┬───────────────────────────────────┘   │
│                    │                                       │
│  ┌─────────────────▼───────────────────────────────────┐   │
│  │              Docker Network                         │   │
│  │                                                     │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │   │
│  │  │  Frontend   │  │   Backend   │  │ PostgreSQL  │ │   │
│  │  │   (React)   │  │(Spring Boot)│  │ Database    │ │   │
│  │  │   nginx     │  │             │  │             │ │   │
│  │  │   :81       │  │   :8081     │  │   :5433     │ │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘ │   │
│  │       │                 │                 │        │   │
│  │       └─────────────────┼─────────────────┘        │   │
│  │                         │                          │   │
│  │              localhost only (127.0.0.1)           │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Network Flow

1. **Client Request** → HTTPS to nginx (port 443)
2. **SSL Termination** → nginx handles SSL/TLS
3. **Routing Decision** → nginx routes based on path:
   - `/api/*` → Backend container (127.0.0.1:8081)
   - `/*` → Frontend container (127.0.0.1:81)
4. **Container Processing** → Services process requests
5. **Response** → Back through nginx to client

## 🔧 Component Architecture

### Frontend (React + TypeScript)

```
frontend/
├── src/
│   ├── components/          # Reusable UI components
│   │   ├── ui/             # Base UI components (buttons, inputs)
│   │   ├── forms/          # Form-specific components
│   │   └── layout/         # Layout components
│   ├── pages/              # Page-level components
│   ├── hooks/              # Custom React hooks
│   ├── services/           # API communication layer
│   ├── types/              # TypeScript type definitions
│   ├── utils/              # Utility functions
│   └── styles/             # Global styles and Tailwind config
├── public/                 # Static assets
└── nginx.conf             # Container nginx configuration
```

**Key Technologies:**
- **React 18**: Modern React with hooks and concurrent features
- **TypeScript**: Type safety and better developer experience
- **Vite**: Fast build tool and development server
- **Tailwind CSS**: Utility-first CSS framework
- **React Hook Form**: Efficient form handling with validation
- **Lucide React**: Modern icon library

### Backend (Spring Boot)

```
backend/
├── src/main/java/no/spond/club/
│   ├── controller/         # REST API controllers
│   │   └── RegistrationController.java
│   ├── service/            # Business logic layer
│   │   └── RegistrationFormService.java
│   ├── dto/                # Data Transfer Objects
│   │   ├── RegistrationFormDto.java
│   │   ├── RegistrationRequestDto.java
│   │   └── MemberTypeDto.java
│   ├── config/             # Configuration classes
│   └── ClubApplication.java # Main application class
├── src/main/resources/
│   ├── application.yml     # Main configuration
│   ├── application-dev.yml # Development configuration
│   └── application-prod.yml # Production configuration
└── src/test/               # Test classes
```

**Key Technologies:**
- **Spring Boot 3.x**: Modern Java framework
- **Spring Web**: REST API development
- **Spring Data JPA**: Database abstraction layer
- **Spring Validation**: Input validation
- **PostgreSQL**: Production database
- **Maven**: Dependency management and build tool

### Database (PostgreSQL)

```sql
-- Core entities (simplified)
registration_forms (
    id BIGSERIAL PRIMARY KEY,
    club_id VARCHAR(255),
    form_id VARCHAR(255) UNIQUE,
    title VARCHAR(255),
    description TEXT,
    registration_opens TIMESTAMP
);

member_types (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255)
);

-- Future: registrations table for actual member data
```

## 🔒 Security Architecture

### Container Security

1. **Non-root Execution**
   - All containers run as non-root users
   - Minimal privileges and capabilities

2. **Network Isolation**
   - Containers bind to localhost only
   - No direct external access
   - Communication through Docker network

3. **Image Security**
   - Multi-stage builds to minimize attack surface
   - Alpine Linux base images for smaller footprint
   - Regular security updates

### Network Security

1. **SSL/TLS Termination**
   - Let's Encrypt certificates
   - Modern TLS configuration
   - Automatic HTTPS redirect

2. **Security Headers**
   ```nginx
   add_header X-Frame-Options "SAMEORIGIN" always;
   add_header X-XSS-Protection "1; mode=block" always;
   add_header X-Content-Type-Options "nosniff" always;
   add_header Referrer-Policy "no-referrer-when-downgrade" always;
   add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;
   ```

3. **CORS Configuration**
   - Restricted to specific origins
   - Proper preflight handling

### Application Security

1. **Input Validation**
   - Server-side validation with Spring Validation
   - Client-side validation with React Hook Form
   - SQL injection prevention with JPA

2. **Error Handling**
   - Structured error responses
   - No sensitive information in error messages
   - Proper HTTP status codes

## 📊 Data Flow

### Registration Flow

```
1. User visits https://spondy.rotchess.com
   ↓
2. Frontend loads from nginx container
   ↓
3. Frontend calls GET /api/form
   ↓
4. nginx routes to backend container
   ↓
5. Backend returns form configuration
   ↓
6. User fills and submits form
   ↓
7. Frontend calls POST /api/form/{id}/register
   ↓
8. Backend validates and processes registration
   ↓
9. Success/error response returned to user
```

### API Design

**RESTful Endpoints:**
- `GET /api/form` - Get default registration form
- `GET /api/form/{id}` - Get specific form by ID
- `POST /api/form/{formId}/register` - Submit registration
- `GET /api/actuator/health` - Health check

**Response Format:**
```json
{
  "success": true|false,
  "message": "Human-readable message",
  "data": { ... },
  "error": "ERROR_CODE",
  "fieldErrors": { ... }
}
```

## 🚀 Deployment Architecture

### Build Process

1. **Multi-stage Docker Builds**
   - Separate build and runtime stages
   - Optimized image sizes
   - Security scanning capabilities

2. **Automated Deployment**
   - Build on MacBook
   - Export as compressed images
   - Transfer and deploy to Raspberry Pi

### Infrastructure

**Raspberry Pi Specifications:**
- **OS**: Ubuntu 24.10 ARM64
- **CPU**: ARM Cortex-A72 (4 cores)
- **RAM**: 4GB+ recommended
- **Storage**: 32GB+ SD card or SSD
- **Network**: Ethernet connection recommended

**Resource Allocation:**
- **PostgreSQL**: ~200MB RAM
- **Backend**: ~400MB RAM
- **Frontend**: ~50MB RAM
- **nginx**: ~10MB RAM

## 🔄 Scalability Considerations

### Current Limitations

- Single server deployment
- No load balancing
- Single database instance
- No caching layer

### Future Scaling Options

1. **Horizontal Scaling**
   - Multiple backend instances behind load balancer
   - Database read replicas
   - CDN for static assets

2. **Vertical Scaling**
   - Upgrade Raspberry Pi hardware
   - Optimize JVM settings
   - Database tuning

3. **Cloud Migration**
   - Container orchestration (Kubernetes)
   - Managed database services
   - Auto-scaling capabilities

## 🔍 Monitoring and Observability

### Current Monitoring

- **Health Checks**: Built-in Spring Boot Actuator
- **Logs**: Docker container logs
- **Metrics**: Basic system metrics

### Future Monitoring

- **Application Metrics**: Prometheus + Grafana
- **Log Aggregation**: ELK Stack
- **Uptime Monitoring**: External monitoring services
- **Performance Monitoring**: APM tools

## 🛠️ Development Architecture

### Local Development

- **Database**: Docker container
- **Backend**: Local Spring Boot or Docker
- **Frontend**: Local Vite dev server or Docker
- **Hot Reload**: Enabled for rapid development

### CI/CD Pipeline (Future)

```
Code Push → GitHub Actions → Build → Test → Deploy → Monitor
```

## 📈 Performance Characteristics

### Response Times (Target)

- **Static Assets**: < 100ms
- **API Calls**: < 500ms
- **Form Submission**: < 1s
- **Page Load**: < 2s

### Throughput (Estimated)

- **Concurrent Users**: 50-100
- **Requests/Second**: 100-200
- **Database Connections**: 10-20

This architecture provides a solid foundation for the Spondy registration system with room for future growth and improvements. 