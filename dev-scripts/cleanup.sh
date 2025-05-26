#!/bin/bash

echo "🧹 Cleaning up all development containers..."

# Stop and remove all development containers
echo "Stopping containers..."
docker stop spondy-postgres-frontend-dev spondy-postgres-dev spondy-backend-dev 2>/dev/null || true

echo "Removing containers..."
docker rm spondy-postgres-frontend-dev spondy-postgres-dev spondy-backend-dev 2>/dev/null || true

# Stop docker-compose services
echo "Stopping docker-compose services..."
docker compose -f docker-compose.dev.yml down 2>/dev/null || true
docker compose -f docker-compose.frontend-dev.yml down 2>/dev/null || true
docker compose down 2>/dev/null || true

# Remove volumes (optional - uncomment if you want to reset database)
# echo "Removing volumes..."
# docker volume rm postgres_dev_data postgres_frontend_dev_data postgres_data 2>/dev/null || true

echo "✅ Cleanup complete!"
echo ""
echo "🚀 To start fresh:"
echo "   Database only:     ./dev-scripts/start-db-only.sh"
echo "   Backend + DB:      ./dev-scripts/start-backend-only.sh"
echo "   Full production:   docker compose up -d --build" 