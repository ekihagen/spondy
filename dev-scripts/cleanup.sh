#!/bin/bash

echo "🧹 Cleaning up all development containers and processes..."

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# Stop local backend and frontend processes
echo "Stopping local processes..."
if [ -f "$PROJECT_DIR/logs/backend.pid" ]; then
    BACKEND_PID=$(cat "$PROJECT_DIR/logs/backend.pid")
    if kill -0 "$BACKEND_PID" 2>/dev/null; then
        echo "Stopping backend process (PID: $BACKEND_PID)..."
        kill "$BACKEND_PID" 2>/dev/null || true
        # Wait a moment for graceful shutdown
        sleep 2
        # Force kill if still running
        kill -9 "$BACKEND_PID" 2>/dev/null || true
    fi
    rm -f "$PROJECT_DIR/logs/backend.pid"
fi

if [ -f "$PROJECT_DIR/logs/frontend.pid" ]; then
    FRONTEND_PID=$(cat "$PROJECT_DIR/logs/frontend.pid")
    if kill -0 "$FRONTEND_PID" 2>/dev/null; then
        echo "Stopping frontend process (PID: $FRONTEND_PID)..."
        kill "$FRONTEND_PID" 2>/dev/null || true
        # Wait a moment for graceful shutdown
        sleep 2
        # Force kill if still running
        kill -9 "$FRONTEND_PID" 2>/dev/null || true
    fi
    rm -f "$PROJECT_DIR/logs/frontend.pid"
fi

# Also kill any remaining Maven or npm processes related to our project
echo "Cleaning up any remaining processes..."
pkill -f "spring-boot:run.*spondy" 2>/dev/null || true
pkill -f "npm run dev.*spondy" 2>/dev/null || true

# Stop and remove all development containers
echo "Stopping containers..."
docker stop spondy-postgres-frontend-dev spondy-postgres-dev spondy-backend-dev spondy-postgres-backend-dev spondy-frontend-backend-dev 2>/dev/null || true

echo "Removing containers..."
docker rm spondy-postgres-frontend-dev spondy-postgres-dev spondy-backend-dev spondy-postgres-backend-dev spondy-frontend-backend-dev 2>/dev/null || true

# Stop docker-compose services
echo "Stopping docker-compose services..."
docker-compose -f docker-compose.dev.yml down 2>/dev/null || true
docker-compose -f docker-compose.frontend-dev.yml down 2>/dev/null || true
docker-compose -f docker-compose.backend-dev.yml down 2>/dev/null || true
docker-compose down 2>/dev/null || true

# Remove volumes (optional - uncomment if you want to reset database)
# echo "Removing volumes..."
# docker volume rm postgres_dev_data postgres_frontend_dev_data postgres_data 2>/dev/null || true

echo "✅ Cleanup complete!"
echo ""
echo "🚀 To start fresh:"
echo "   Fullstack:         spondy fullstack"
echo "   Frontend dev:      spondy frontend"
echo "   Backend dev:       spondy backend"
echo "   Production:        spondy production" 