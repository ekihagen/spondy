#!/bin/bash

echo "🗄️ Starting PostgreSQL + Frontend in Docker..."
echo "📝 Use this when you want to develop the backend locally"

# Stop any existing containers
docker stop spondy-postgres-backend-dev spondy-frontend-backend-dev 2>/dev/null || true
docker rm spondy-postgres-backend-dev spondy-frontend-backend-dev 2>/dev/null || true

# Build and start database + frontend
docker-compose -f docker-compose.backend-dev.yml up -d --build

echo "✅ PostgreSQL + Frontend started!"
echo ""
echo "🚀 Now you can run:"
echo "   Backend: cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev"
echo ""
echo "🌐 URLs:"
echo "   Frontend: http://localhost:3000"
echo "   Backend:  http://localhost:8080 (run locally)"
echo "   Database: localhost:5432"
echo ""
echo "📊 Check status:"
echo "   docker-compose -f docker-compose.backend-dev.yml ps"
echo "   docker-compose -f docker-compose.backend-dev.yml logs -f" 