# Development Modes Quick Reference

This project supports multiple development modes to accommodate different development workflows.

## 🎯 Quick Commands (Recommended)

Use the unified `spondy` command for the best experience:

```bash
spondy fullstack   # Database only (both backend + frontend local)
spondy frontend    # Backend + database (frontend local development)
spondy backend     # Frontend + database (backend local development)
spondy production  # All services in Docker (production mode)
spondy status      # Show container status
spondy cleanup     # Clean up everything
spondy help        # Show all commands
```

## 🚀 Available Development Modes

### 1. **Fullstack Development** (`spondy fullstack`)
- **PostgreSQL**: Docker container (port 5432)
- **Backend**: Local development server (port 8080)
- **Frontend**: Local Vite dev server (port 5173)
- **Use case**: Full local development with hot reload for both frontend and backend

### 2. **Frontend Development** (`spondy frontend`)
- **PostgreSQL**: Docker container (port 5432)
- **Backend**: Docker container (port 8080)
- **Frontend**: Local Vite dev server (port 5173)
- **Use case**: Frontend development with stable backend environment

### 3. **Backend Development** (`spondy backend`)
- **PostgreSQL**: Docker container (port 5432)
- **Backend**: Local development server (port 8080)
- **Frontend**: Docker container (port 3000)
- **Use case**: Backend development with stable frontend environment

### 4. **Production Mode** (`spondy production`)
- **PostgreSQL**: Docker container (port 5432)
- **Backend**: Docker container (port 8080)
- **Frontend**: Docker container (port 3000)
- **Use case**: Production-like testing and deployment

## 🛠️ Manual Commands (Alternative)

If you prefer to use the individual scripts directly:

```bash
./dev-scripts/start-db-only.sh        # Same as spondy fullstack
./dev-scripts/start-backend-only.sh   # Same as spondy frontend
./dev-scripts/start-frontend-only.sh  # Same as spondy backend
docker-compose up -d --build          # Same as spondy production
./dev-scripts/cleanup.sh              # Same as spondy cleanup
```

## 🧹 Cleanup & Status

```bash
spondy cleanup    # Stop and remove all development containers
spondy status     # Show running containers and volumes
spondy logs       # Show recent logs from running containers
```

## 📁 Docker Compose Files

| File | Purpose |
|------|---------|
| `docker-compose.yml` | Full production setup |
| `docker-compose.dev.yml` | Backend + Database for frontend development |
| `docker-compose.frontend-dev.yml` | Database only for full local development |
| `docker-compose.backend-dev.yml` | Frontend + Database for backend development |
| `docker-compose.prod.yml` | Production deployment configuration |

## 🌐 Port Mappings

| Service | Local Dev | Docker Dev | Production |
|---------|-----------|------------|------------|
| Frontend | 5173 | 3000 | 8082 |
| Backend | 8080 | 8080 | 8081 |
| Database | 5432 | 5432 | 5433 |

## 💡 Tips

- **Hot Reload**: Only available when running services locally (not in Docker)
- **API Configuration**: Frontend automatically detects environment and uses appropriate API URL
- **Database**: Each mode uses separate Docker volumes to avoid conflicts
- **Cleanup**: Always run cleanup script when switching between modes 