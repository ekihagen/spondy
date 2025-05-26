#!/bin/bash

echo "🚀 Starting Spond Club Membership Application..."

# Stop any existing containers
echo "📦 Stopping existing containers..."
docker-compose down

# Build and start all services
echo "🔨 Building and starting services..."
docker-compose up --build

echo "✅ Application started!"
echo "🌐 Frontend: http://localhost:3000"
echo "🔧 Backend API: http://localhost:8080"
echo "🗄️ Database: localhost:5432" 