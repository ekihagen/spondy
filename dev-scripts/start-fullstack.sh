#!/bin/bash

set -e

echo "🚀 Starting fullstack development environment..."
echo "📝 Database in Docker, backend + frontend starting locally"

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# Stop any existing containers
echo "🧹 Cleaning up existing containers..."
docker stop spondy-postgres-frontend-dev 2>/dev/null || true
docker rm spondy-postgres-frontend-dev 2>/dev/null || true

# Start PostgreSQL
echo "🗄️ Starting PostgreSQL database..."
docker run -d \
  --name spondy-postgres-frontend-dev \
  -e POSTGRES_DB=spondy_club \
  -e POSTGRES_USER=spondy \
  -e POSTGRES_PASSWORD=spondy123 \
  -p 5432:5432 \
  postgres:15-alpine

# Wait for database to be ready
echo "⏳ Waiting for database to be ready..."
sleep 5

# Check if backend dependencies are installed
echo "🔧 Checking backend dependencies..."
cd "$PROJECT_DIR/backend"
if [ ! -d "target" ]; then
    echo "📦 Installing backend dependencies..."
    ./mvnw clean compile -q
fi

# Check if frontend dependencies are installed
echo "🔧 Checking frontend dependencies..."
cd "$PROJECT_DIR/frontend"
if [ ! -d "node_modules" ]; then
    echo "📦 Installing frontend dependencies..."
    npm install
fi

# Create log directory
mkdir -p "$PROJECT_DIR/logs"

# Set up Java environment
echo "🔧 Setting up Java environment..."
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"

# Verify Java is available
if ! command -v java &> /dev/null; then
    echo "❌ Java not found. Please install Java 17 or later."
    echo "   Try: brew install openjdk@17"
    exit 1
fi

echo "✅ Java found: $(java -version 2>&1 | head -n 1)"

# Start backend in background
echo "🚀 Starting backend..."
cd "$PROJECT_DIR/backend"
nohup env JAVA_HOME="$JAVA_HOME" PATH="$PATH" ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev > "$PROJECT_DIR/logs/backend.log" 2>&1 &
BACKEND_PID=$!
echo $BACKEND_PID > "$PROJECT_DIR/logs/backend.pid"

# Wait a moment for backend to start
sleep 3

# Start frontend in background
echo "🚀 Starting frontend..."
cd "$PROJECT_DIR/frontend"
nohup npm run dev > "$PROJECT_DIR/logs/frontend.log" 2>&1 &
FRONTEND_PID=$!
echo $FRONTEND_PID > "$PROJECT_DIR/logs/frontend.pid"

# Wait a moment to get the actual frontend port
sleep 2

# Try to detect the actual frontend port
FRONTEND_PORT=5173
if ! curl -s http://localhost:5173 > /dev/null 2>&1; then
    if curl -s http://localhost:5174 > /dev/null 2>&1; then
        FRONTEND_PORT=5174
    fi
fi

echo ""
echo "✅ Fullstack environment started!"
echo ""
echo "🌐 URLs:"
echo "   Frontend: http://localhost:$FRONTEND_PORT"
echo "   Backend:  http://localhost:8080"
echo "   Database: localhost:5432"
echo ""
echo "📋 Process IDs:"
echo "   Backend PID:  $BACKEND_PID"
echo "   Frontend PID: $FRONTEND_PID"
echo ""
echo "📝 Logs:"
echo "   Backend:  tail -f logs/backend.log"
echo "   Frontend: tail -f logs/frontend.log"
echo ""
echo "🛑 To stop all services:"
echo "   spondy cleanup"
echo ""
echo "💡 Tip: Use 'spondy logs' to see recent logs from all services" 