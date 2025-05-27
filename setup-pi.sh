#!/bin/bash

# Spondy Pi Setup Script
# Run this script on your Raspberry Pi after copying over the files

set -e

echo "🍓 Spondy Raspberry Pi Setup Script"
echo "=================================="

# Check if running as root
if [ "$EUID" -eq 0 ]; then
    echo "⚠️  Please don't run this script as root"
    echo "   Run as your regular user instead"
    exit 1
fi

DEPLOY_DIR="/home/$(whoami)/spondy-prod"
echo "📁 Working directory: $DEPLOY_DIR"

# Create deployment directory if it doesn't exist
mkdir -p "$DEPLOY_DIR"
cd "$DEPLOY_DIR"

echo ""
echo "🔍 Checking system requirements..."

# Check if Docker is installed
if ! command -v docker >/dev/null 2>&1; then
    echo "❌ Docker not found. Installing Docker..."
    
    # Update package list
    sudo apt update
    
    # Install required packages
    sudo apt install -y ca-certificates curl gnupg lsb-release
    
    # Add Docker's official GPG key
    sudo mkdir -p /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
    
    # Set up the repository
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    
    # Install Docker Engine
    sudo apt update
    sudo apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
    
    # Add user to docker group
    sudo usermod -aG docker $USER
    
    echo "✅ Docker installed successfully"
    echo "⚠️  You need to log out and back in for Docker permissions to take effect"
    echo "   After logging back in, run this script again"
    exit 0
else
    echo "✅ Docker is installed"
fi

# Check if docker-compose is available
if ! command -v docker-compose >/dev/null 2>&1 && ! docker compose version >/dev/null 2>&1; then
    echo "❌ docker-compose not found. Installing..."
    sudo apt update
    sudo apt install -y docker-compose
    echo "✅ docker-compose installed"
else
    echo "✅ docker-compose is available"
fi

# Check if user is in docker group
if ! groups $USER | grep -q docker; then
    echo "❌ User not in docker group. Adding..."
    sudo usermod -aG docker $USER
    echo "⚠️  You need to log out and back in for Docker permissions to take effect"
    echo "   After logging back in, run this script again"
    exit 0
else
    echo "✅ User has Docker permissions"
fi

echo ""
echo "📦 Checking for Docker images..."

# Check if image files exist
BACKEND_IMAGE="spondy-backend-prod.tar.gz"
FRONTEND_IMAGE="spondy-frontend-prod.tar.gz"
POSTGRES_IMAGE="postgres-15-alpine.tar.gz"

missing_files=()
[ ! -f "$BACKEND_IMAGE" ] && missing_files+=("$BACKEND_IMAGE")
[ ! -f "$FRONTEND_IMAGE" ] && missing_files+=("$FRONTEND_IMAGE")
[ ! -f "$POSTGRES_IMAGE" ] && missing_files+=("$POSTGRES_IMAGE")

if [ ${#missing_files[@]} -ne 0 ]; then
    echo "❌ Missing Docker image files:"
    for file in "${missing_files[@]}"; do
        echo "   - $file"
    done
    echo ""
    echo "Please copy these files to $DEPLOY_DIR first:"
    echo "   scp spondy-backend-prod.tar.gz $USER@$(hostname -I | awk '{print $1}'):$DEPLOY_DIR/"
    echo "   scp spondy-frontend-prod.tar.gz $USER@$(hostname -I | awk '{print $1}'):$DEPLOY_DIR/"
    echo "   scp postgres-15-alpine.tar.gz $USER@$(hostname -I | awk '{print $1}'):$DEPLOY_DIR/"
    echo "   scp docker-compose.prod.yml $USER@$(hostname -I | awk '{print $1}'):$DEPLOY_DIR/"
    echo "   scp production.env $USER@$(hostname -I | awk '{print $1}'):$DEPLOY_DIR/"
    exit 1
fi

echo "✅ All Docker image files found"

# Check for configuration files
if [ ! -f "docker-compose.prod.yml" ]; then
    echo "❌ docker-compose.prod.yml not found"
    echo "   Please copy it to $DEPLOY_DIR"
    exit 1
fi

echo "✅ Configuration files found"

echo ""
echo "📥 Loading Docker images..."

echo "  - Loading PostgreSQL image..."
docker load < "$POSTGRES_IMAGE"

echo "  - Loading backend image..."
docker load < "$BACKEND_IMAGE"

echo "  - Loading frontend image..."
docker load < "$FRONTEND_IMAGE"

echo "✅ All images loaded successfully"

echo ""
echo "🛑 Stopping existing containers (if any)..."
docker-compose -f docker-compose.prod.yml down 2>/dev/null || echo "  (No existing containers to stop)"

echo ""
echo "🚀 Starting Spondy application..."
docker-compose -f docker-compose.prod.yml up -d

echo ""
echo "⏳ Waiting for services to start..."
sleep 30

echo ""
echo "🔍 Checking service status..."
docker-compose -f docker-compose.prod.yml ps

echo ""
echo "🏥 Testing health endpoints..."

# Get the local IP
LOCAL_IP=$(hostname -I | awk '{print $1}')

if curl -f http://localhost/api/actuator/health >/dev/null 2>&1; then
    echo "  ✅ Backend health check passed"
else
    echo "  ⚠️  Backend health check failed (may still be starting)"
    echo "     Check logs with: docker-compose -f docker-compose.prod.yml logs backend"
fi

if curl -f http://localhost/ >/dev/null 2>&1; then
    echo "  ✅ Frontend health check passed"
else
    echo "  ⚠️  Frontend health check failed (may still be starting)"
    echo "     Check logs with: docker-compose -f docker-compose.prod.yml logs frontend"
fi

echo ""
echo "🎉 Deployment complete!"
echo ""
echo "📊 Application URLs:"
echo "  Frontend: http://$LOCAL_IP/"
echo "  Backend API: http://$LOCAL_IP/api/"
echo "  Health Check: http://$LOCAL_IP/api/actuator/health"
echo ""
echo "🔧 Useful commands:"
echo "  View all logs: docker-compose -f docker-compose.prod.yml logs"
echo "  View backend logs: docker-compose -f docker-compose.prod.yml logs backend"
echo "  View frontend logs: docker-compose -f docker-compose.prod.yml logs frontend"
echo "  Stop application: docker-compose -f docker-compose.prod.yml down"
echo "  Restart application: docker-compose -f docker-compose.prod.yml restart"
echo "  Update application: docker-compose -f docker-compose.prod.yml pull && docker-compose -f docker-compose.prod.yml up -d"
echo ""
echo "📝 Configuration files in: $DEPLOY_DIR"
echo "   - docker-compose.prod.yml (main configuration)"
echo "   - production.env (environment variables)"
echo ""
