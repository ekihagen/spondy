#!/bin/bash

# Secure Deployment script for Spondy to Raspberry Pi
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CONFIG_FILE="$SCRIPT_DIR/deploy.config"

echo "=== Spondy Production Deployment to Raspberry Pi ==="

# Load configuration
if [ -f "$CONFIG_FILE" ]; then
    echo "📋 Loading configuration from deploy.config..."
    source "$CONFIG_FILE"
elif [ -f "$SCRIPT_DIR/.env.deploy" ]; then
    echo "📋 Loading configuration from .env.deploy..."
    source "$SCRIPT_DIR/.env.deploy"
else
    echo "⚠️  No configuration file found!"
    echo ""
    echo "Please create one of the following:"
    echo "  1. Copy deploy.config.template to deploy.config and fill in your details"
    echo "  2. Create .env.deploy with your deployment variables"
    echo "  3. Set environment variables directly:"
    echo ""
    echo "Required variables:"
    echo "  PI_HOST=your.pi.ip.address"
    echo "  PI_USER=your-username"
    echo "  PI_PASSWORD=your-password (or PI_SSH_KEY=path/to/key)"
    echo "  DEPLOY_DIR=/path/on/pi"
    echo ""
    echo "Example:"
    echo "  export PI_HOST=192.168.1.100"
    echo "  export PI_USER=pi"
    echo "  export PI_PASSWORD=your-password"
    echo "  export DEPLOY_DIR=/home/pi/spondy-prod"
    echo "  ./deploy-to-pi-secure.sh"
    exit 1
fi

# Validate required variables
missing_vars=()
[ -z "$PI_HOST" ] && missing_vars+=("PI_HOST")
[ -z "$PI_USER" ] && missing_vars+=("PI_USER")
[ -z "$DEPLOY_DIR" ] && missing_vars+=("DEPLOY_DIR")

if [ -z "$PI_PASSWORD" ] && [ -z "$PI_SSH_KEY" ]; then
    missing_vars+=("PI_PASSWORD or PI_SSH_KEY")
fi

if [ ${#missing_vars[@]} -ne 0 ]; then
    echo "❌ Missing required variables: ${missing_vars[*]}"
    exit 1
fi

echo "🎯 Deploying to: $PI_USER@$PI_HOST:$DEPLOY_DIR"

# Function to run SSH commands
ssh_cmd() {
    if [ -n "$PI_SSH_KEY" ]; then
        # Use SSH key authentication
        ssh -i "$PI_SSH_KEY" -o StrictHostKeyChecking=no "$PI_USER@$PI_HOST" "$1"
    else
        # Use password authentication
        if ! command -v sshpass >/dev/null 2>&1; then
            echo "❌ sshpass is required for password authentication"
            echo "Install with: brew install sshpass (macOS) or apt-get install sshpass (Linux)"
            echo "Or use SSH key authentication instead by setting PI_SSH_KEY"
            exit 1
        fi
        sshpass -p "$PI_PASSWORD" ssh -F /dev/null -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o PreferredAuthentications=password -o PubkeyAuthentication=no "$PI_USER@$PI_HOST" "$1"
    fi
}

# Function to copy files
scp_file() {
    if [ -n "$PI_SSH_KEY" ]; then
        # Use SSH key authentication
        scp -i "$PI_SSH_KEY" -o StrictHostKeyChecking=no "$1" "$PI_USER@$PI_HOST:$2"
    else
        # Use password authentication
        sshpass -p "$PI_PASSWORD" scp -F /dev/null -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o PreferredAuthentications=password -o PubkeyAuthentication=no "$1" "$PI_USER@$PI_HOST:$2"
    fi
}

echo "📡 Testing connection to Raspberry Pi..."
if ! ssh_cmd "echo 'Connection successful'"; then
    echo "❌ Failed to connect to Raspberry Pi"
    echo "Please check your credentials and network connection"
    exit 1
fi

echo "📁 Creating deployment directory on Pi..."
ssh_cmd "mkdir -p $DEPLOY_DIR"

echo "📤 Transferring Docker images..."
echo "  - Transferring backend image (231MB)..."
scp_file "spondy-backend-prod.tar.gz" "$DEPLOY_DIR/"

echo "  - Transferring frontend image (20MB)..."
scp_file "spondy-frontend-prod.tar.gz" "$DEPLOY_DIR/"

echo "  - Transferring PostgreSQL image (96MB)..."
scp_file "postgres-15-alpine.tar.gz" "$DEPLOY_DIR/"

echo "📤 Transferring configuration files..."
scp_file "docker-compose.prod.yml" "$DEPLOY_DIR/"
scp_file "production.env" "$DEPLOY_DIR/" 2>/dev/null || echo "  (production.env not found, using defaults)"
scp_file "setup-pi.sh" "$DEPLOY_DIR/"
scp_file "spondy-nginx-site.conf" "$DEPLOY_DIR/" 2>/dev/null || echo "  (nginx config not found)"

echo "🔧 Setting up Docker on Pi (if not already installed)..."
ssh_cmd "cd $DEPLOY_DIR && chmod +x setup-pi.sh"

# Check if Docker is installed
if ! ssh_cmd "command -v docker >/dev/null 2>&1"; then
    echo "  ⚠️  Docker not found. Please install Docker manually on the Pi:"
    echo "     1. Run: cd $DEPLOY_DIR && chmod +x setup-pi.sh && ./setup-pi.sh"
    echo "     2. Log out and back in"
    echo "     3. Run this script again"
    exit 1
else
    echo "  ✅ Docker already installed"
fi

echo "📦 Loading Docker images on Pi..."
ssh_cmd "cd $DEPLOY_DIR && docker load < postgres-15-alpine.tar.gz"
ssh_cmd "cd $DEPLOY_DIR && docker load < spondy-backend-prod.tar.gz"
ssh_cmd "cd $DEPLOY_DIR && docker load < spondy-frontend-prod.tar.gz"

echo "🛑 Stopping existing containers (if any)..."
ssh_cmd "cd $DEPLOY_DIR && docker-compose -f docker-compose.prod.yml down" 2>/dev/null || echo "  (No existing containers to stop)"

echo "🚀 Starting Spondy application..."
ssh_cmd "cd $DEPLOY_DIR && docker-compose -f docker-compose.prod.yml up -d"

echo "⏳ Waiting for services to start..."
sleep 30

echo "🔍 Checking service status..."
ssh_cmd "cd $DEPLOY_DIR && docker-compose -f docker-compose.prod.yml ps"

echo "🏥 Testing health endpoints..."
if ssh_cmd "curl -f http://localhost/api/actuator/health >/dev/null 2>&1"; then
    echo "  ✅ Backend health check passed"
else
    echo "  ⚠️  Backend health check failed (may still be starting)"
fi

if ssh_cmd "curl -f http://localhost/ >/dev/null 2>&1"; then
    echo "  ✅ Frontend health check passed"
else
    echo "  ⚠️  Frontend health check failed (may still be starting)"
fi

echo ""
echo "🎉 Deployment complete!"
echo ""
echo "📊 Application URLs:"
echo "  Frontend: http://$PI_HOST/"
echo "  Backend API: http://$PI_HOST/api/"
echo "  Health Check: http://$PI_HOST/api/actuator/health"
echo ""
echo "🔧 Management commands:"
echo "  View logs: ssh $PI_USER@$PI_HOST 'cd $DEPLOY_DIR && docker-compose -f docker-compose.prod.yml logs'"
echo "  Stop app: ssh $PI_USER@$PI_HOST 'cd $DEPLOY_DIR && docker-compose -f docker-compose.prod.yml down'"
echo "  Start app: ssh $PI_USER@$PI_HOST 'cd $DEPLOY_DIR && docker-compose -f docker-compose.prod.yml up -d'"
echo "" 