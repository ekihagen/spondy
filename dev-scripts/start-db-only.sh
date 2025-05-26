#!/bin/bash

echo "🗄️ Starting PostgreSQL database only..."
echo "📝 Use this when you want to run both backend and frontend locally"

# Stop any existing containers
docker stop spondy-postgres-frontend-dev 2>/dev/null || true
docker rm spondy-postgres-frontend-dev 2>/dev/null || true

# Start only PostgreSQL
docker run -d \
  --name spondy-postgres-frontend-dev \
  -e POSTGRES_DB=spondy_club \
  -e POSTGRES_USER=spondy \
  -e POSTGRES_PASSWORD=spondy123 \
  -p 5432:5432 \
  postgres:15-alpine

echo "✅ PostgreSQL started!"
echo ""
echo "🚀 Now you can run:"
echo "   Backend:  cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev"
echo "   Frontend: cd frontend && npm run dev"
echo ""
echo "🌐 URLs:"
echo "   Frontend: http://localhost:5173"
echo "   Backend:  http://localhost:8080"
echo "   Database: localhost:5432" 