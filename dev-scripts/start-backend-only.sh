#!/bin/bash

echo "🗄️ Starting PostgreSQL + Backend in Docker..."
echo "📝 Use this when you want to develop the frontend locally"

# Stop any existing containers
docker stop spondy-postgres-dev spondy-backend-dev 2>/dev/null || true
docker rm spondy-postgres-dev spondy-backend-dev 2>/dev/null || true

# Build and start database + backend
docker compose -f docker-compose.dev.yml up -d --build

echo "✅ PostgreSQL + Backend started!"
echo ""
echo "🚀 Now you can run:"
echo "   Frontend: cd frontend && npm run dev"
echo ""
echo "🌐 URLs:"
echo "   Frontend: http://localhost:5173"
echo "   Backend:  http://localhost:8080"
echo "   Database: localhost:5432"
echo ""
echo "📊 Check status:"
echo "   docker compose -f docker-compose.dev.yml ps"
echo "   docker compose -f docker-compose.dev.yml logs -f" 