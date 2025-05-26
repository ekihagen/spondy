# Spond Club - Medlemskapsregistrering

En full-stack applikasjon for å håndtere offentlige medlemskapsregistreringsskjemaer for klubber.

🌐 **Live produksjon**: https://spondy.rotchess.com

## Teknologivalg

### Backend
- **Spring Boot 3.x** med Java 17 - Robust og moden framework for rask utvikling
- **PostgreSQL** - Pålitelig relationsdatabase
- **Spring Data JPA** - Forenkler databaseoperasjoner
- **Spring Validation** - Innebygd validering
- **Docker Compose** - For enkel lokal utvikling og produksjon

### Frontend  
- **React 18** med TypeScript - Moderne komponentbasert utvikling
- **Vite** - Rask build-tool og dev server
- **Tailwind CSS** - Utility-first CSS framework for rask styling
- **Lucide React** - Moderne ikoner
- **React Hook Form** - Effektiv form-håndtering med validering

### Produksjon
- **Docker** - Containerisering for konsistent deployment
- **nginx** - Reverse proxy med SSL/TLS terminering
- **Let's Encrypt** - Automatiske SSL-sertifikater
- **Raspberry Pi** - Ubuntu 24.10 ARM64

## Kjøring av applikasjonen

### Forutsetninger
- Docker
- Node.js 18+ og npm/yarn (for lokal frontend utvikling)
- Java 17+ og Maven (for lokal backend utvikling)

### Utviklingsalternativer

Vi tilbyr flere fleksible måter å kjøre applikasjonen på, avhengig av hva du jobber med:

#### 1. Full produksjon (alle tjenester i Docker)
```bash
# Start alt i Docker
docker compose up -d --build

# URLs:
# Frontend: http://localhost:3000
# Backend: http://localhost:8080
# Database: localhost:5432
```

#### 2. Frontend-utvikling (backend + DB i Docker)
```bash
# Start backend og database i Docker
./dev-scripts/start-backend-only.sh

# Start frontend lokalt (i ny terminal)
cd frontend
npm install
npm run dev

# URLs:
# Frontend: http://localhost:5173 (lokal utvikling)
# Backend: http://localhost:8080 (Docker)
```

#### 3. Backend-utvikling (frontend + DB i Docker)
```bash
# Start frontend og database i Docker
./dev-scripts/start-frontend-only.sh

# Start backend lokalt (i ny terminal)
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# URLs:
# Frontend: http://localhost:3000 (Docker)
# Backend: http://localhost:8080 (lokal utvikling)
# Database: localhost:5432 (Docker)
```

#### 4. Full lokal utvikling (kun DB i Docker)
```bash
# Start kun database i Docker
./dev-scripts/start-db-only.sh

# Start backend lokalt (i ny terminal)
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Start frontend lokalt (i ny terminal)
cd frontend
npm install
npm run dev

# URLs:
# Frontend: http://localhost:5173
# Backend: http://localhost:8080
# Database: localhost:5432 (Docker)
```

#### 5. Rydde opp
```bash
# Stopp og fjern alle utviklingscontainere
./dev-scripts/cleanup.sh
```

### Hurtigstart (anbefalt for nye utviklere)

1. **Klon repositoryet:**
   ```bash
   git clone <repository-url>
   cd spondy
   ```

2. **Start fullstack development:**
   ```bash
   spondy fullstack
   ```

3. **Start backend (i ny terminal):**
   ```bash
   cd backend
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

4. **Start frontend (i ny terminal):**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

5. **Åpne applikasjonen:**
   - Frontend: http://localhost:5173
   - Backend API: http://localhost:8080

### 🚀 Spondy Command (Anbefalt)

For enklere utvikling kan du bruke den nye `spondy` kommandoen:

```bash
spondy help        # Vis alle kommandoer
spondy fullstack   # Full lokal utvikling
spondy frontend    # Frontend-utvikling
spondy backend     # Backend-utvikling
spondy production  # Produksjonsmodus
spondy status      # Vis container-status
spondy cleanup     # Rydd opp alt
```

### Testing

**Backend tester:**
```bash
cd backend
./mvnw test
```

**Frontend tester:**
```bash
cd frontend
npm run test
```

## Funksjonalitet

- ✅ Responsive wizard-UI med 3 steg
- ✅ Validering av brukerinput
- ✅ Håndtering av fremtidige registreringsdatoer
- ✅ Suksess-melding ved vellykket registrering
- ✅ REST API med validering
- ✅ Persistering i PostgreSQL database

## Arkitektur

### Backend struktur
```
backend/
├── src/main/java/no/spond/club/
│   ├── controller/     # REST controllers
│   ├── service/        # Business logic
│   ├── repository/     # Data access
│   ├── model/          # JPA entities
│   └── dto/            # Data transfer objects
└── src/test/           # Tests
```

### Frontend struktur
```
frontend/
├── src/
│   ├── components/     # React komponenter
│   ├── pages/          # Sider
│   ├── hooks/          # Custom hooks
│   ├── services/       # API calls
│   ├── types/          # TypeScript types
│   └── utils/          # Hjelpefunksjoner
└── tests/              # Tests
```

## Produksjonsdeployment

### Rask deployment til Raspberry Pi
```bash
# Bygg produksjonsbilder
docker build -t spondy-backend:prod ./backend
docker build -t spondy-frontend:prod ./frontend

# Sikker deployment (anbefalt)
cp deploy.config.template deploy.config
# Rediger deploy.config med dine verdier
./deploy-to-pi-secure.sh
```

**Sikkerhet:** Se [DEPLOYMENT_SECURITY.md](DEPLOYMENT_SECURITY.md) for sikker deployment uten å eksponere credentials.

### Produksjonsarkitektur
- **Host nginx** (port 80/443) med SSL-terminering
- **Docker containers** på localhost:
  - PostgreSQL: `127.0.0.1:5433`
  - Backend: `127.0.0.1:8081`
  - Frontend: `127.0.0.1:81`

### Dokumentasjon
- **[docs/](docs/)** - Komplett dokumentasjon
- **[Development Guide](docs/development.md)** - Lokal utvikling
- **[Production Deployment](docs/deployment.md)** - Produksjonsdeployment
- **[Nginx Configuration](docs/nginx.md)** - Reverse proxy oppsett
