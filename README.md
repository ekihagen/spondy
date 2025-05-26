# Spondy Club - Membership Registration

A full-stack application for handling public membership registration forms for clubs and organizations.

🌐 **Live Production**: https://spondy.rotchess.com

## 🚀 Quick Development Setup

### Prerequisites
- **Docker** - For database and containerized services
- **Node.js 18+** and npm/yarn - For frontend development
- **Java 17+** - For backend development (automatically detected if installed via Homebrew)

### ⚡ One-Command Setup (Recommended)

```bash
# Clone the repository
git clone <repository-url>
cd spondy

# Start everything with one command
spondy fullstack
```

This will automatically:
- ✅ Start PostgreSQL database in Docker
- ✅ Start Spring Boot backend locally with Java 17
- ✅ Start React frontend locally with Vite
- ✅ Set up all necessary environment variables

**URLs after startup:**
- 🌐 Frontend: http://localhost:5173 (or 5174 if 5173 is busy)
- 🔧 Backend API: http://localhost:8080
- 🗄️ Database: localhost:5432

### 📋 Development Commands

```bash
spondy help        # Show all available commands
spondy fullstack   # Full local development (recommended)
spondy frontend    # Frontend development (backend in Docker)
spondy backend     # Backend development (frontend in Docker)
spondy production  # Production mode (all in Docker)
spondy status      # Show service status
spondy logs        # View logs from all services
spondy cleanup     # Stop and clean up all services
```

### 🔧 Manual Setup (Alternative)

If you prefer manual control:

```bash
# 1. Start database only
./dev-scripts/start-db-only.sh

# 2. Start backend (in new terminal)
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# 3. Start frontend (in new terminal)
cd frontend
npm install
npm run dev
```

## 🛠️ Technology Stack

### Backend
- **Spring Boot 3.x** with Java 17 - Robust and mature framework for rapid development
- **PostgreSQL** - Reliable relational database
- **Spring Data JPA** - Simplified database operations
- **Spring Validation** - Built-in validation
- **Maven** - Dependency management and build tool

### Frontend  
- **React 18** with TypeScript - Modern component-based development
- **Vite** - Fast build tool and dev server
- **Tailwind CSS** - Utility-first CSS framework for rapid styling
- **Lucide React** - Modern icons
- **React Hook Form** - Efficient form handling with validation

### Production
- **Docker** - Containerization for consistent deployment
- **nginx** - Reverse proxy with SSL/TLS termination
- **Let's Encrypt** - Automatic SSL certificates
- **Raspberry Pi** - Ubuntu 24.10 ARM64

## 🎯 Features

- ✅ Responsive wizard UI with 3 steps
- ✅ Input validation and error handling
- ✅ Future registration date handling
- ✅ Success confirmation upon registration
- ✅ REST API with comprehensive validation
- ✅ PostgreSQL data persistence
- ✅ Modern, accessible user interface
- ✅ Mobile-responsive design

## 🏗️ Architecture

### Backend Structure
```
backend/
├── src/main/java/no/spond/club/
│   ├── controller/     # REST controllers
│   ├── service/        # Business logic
│   ├── repository/     # Data access layer
│   ├── model/          # JPA entities
│   ├── dto/            # Data transfer objects
│   └── config/         # Configuration classes
└── src/test/           # Unit and integration tests
```

### Frontend Structure
```
frontend/
├── src/
│   ├── components/     # React components
│   ├── pages/          # Page components
│   ├── hooks/          # Custom React hooks
│   ├── services/       # API service calls
│   ├── types/          # TypeScript type definitions
│   └── utils/          # Utility functions
└── tests/              # Frontend tests
```

## 🧪 Testing

### Backend Tests
```bash
cd backend
./mvnw test
```

### Frontend Tests
```bash
cd frontend
npm run test
```

## 🚀 Production Deployment

### Quick Deployment to Raspberry Pi
```bash
# Build production images
docker build -t spondy-backend:prod ./backend
docker build -t spondy-frontend:prod ./frontend

# Secure deployment (recommended)
cp deploy.config.template deploy.config
# Edit deploy.config with your values
./deploy-to-pi-secure.sh
```

### Production Architecture
- **Host nginx** (ports 80/443) with SSL termination
- **Docker containers** on localhost:
  - PostgreSQL: `127.0.0.1:5433`
  - Backend: `127.0.0.1:8081`
  - Frontend: `127.0.0.1:81`

## 📚 Development Modes

The project supports multiple development modes depending on what you're working on:

### 1. Full Production (All services in Docker)
```bash
docker compose up -d --build
# Frontend: http://localhost:3000
# Backend: http://localhost:8080
# Database: localhost:5432
```

### 2. Frontend Development (Backend + DB in Docker)
```bash
spondy frontend
# Frontend: http://localhost:5173 (local development)
# Backend: http://localhost:8080 (Docker)
```

### 3. Backend Development (Frontend + DB in Docker)
```bash
spondy backend
# Frontend: http://localhost:3000 (Docker)
# Backend: http://localhost:8080 (local development)
```

### 4. Full Local Development (Only DB in Docker)
```bash
spondy fullstack
# Frontend: http://localhost:5173 (local)
# Backend: http://localhost:8080 (local)
# Database: localhost:5432 (Docker)
```

## 🔧 Troubleshooting

### Java Issues
If you get Java-related errors:
```bash
# Install Java 17 via Homebrew (macOS)
brew install openjdk@17

# Or check if Java is properly installed
java -version
```

### Port Conflicts
If ports are busy, the system will automatically use alternative ports:
- Frontend: 5173 → 5174
- Backend: 8080 (fixed)
- Database: 5432 (fixed)

### Database Connection Issues
```bash
# Check if database is running
spondy status

# View logs for debugging
spondy logs

# Clean restart
spondy cleanup
spondy fullstack
```

## 📖 Documentation

- **[docs/](docs/)** - Complete documentation
- **[Development Guide](docs/development.md)** - Local development setup
- **[Production Deployment](docs/deployment.md)** - Production deployment guide
- **[Nginx Configuration](docs/nginx.md)** - Reverse proxy setup

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
