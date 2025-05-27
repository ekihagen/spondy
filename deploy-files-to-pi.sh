#!/bin/bash

# Simple file transfer script for Spondy deployment
# This script just copies files to the Pi, then you run setup-pi.sh on the Pi

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CONFIG_FILE="$SCRIPT_DIR/deploy.config"

echo "📤 Spondy File Transfer to Raspberry Pi"
echo "======================================="

# Load configuration
if [ -f "$CONFIG_FILE" ]; then
    echo "📋 Loading configuration from deploy.config..."
    source "$CONFIG_FILE"
else
    echo "❌ No deploy.config found!"
    echo "Please create deploy.config with your Pi details:"
    echo "  PI_HOST=192.168.x.x"
    echo "  PI_USER=your-username"
    echo "  PI_PASSWORD=your-password (or PI_SSH_KEY=path/to/key)"
    echo "  DEPLOY_DIR=/home/your-username/spondy-prod"
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

echo "🎯 Copying files to: $PI_USER@$PI_HOST:$DEPLOY_DIR"

# Function to copy files
scp_file() {
    if [ -n "$PI_SSH_KEY" ]; then
        scp -i "$PI_SSH_KEY" -o StrictHostKeyChecking=no "$1" "$PI_USER@$PI_HOST:$2"
    else
        if ! command -v sshpass >/dev/null 2>&1; then
            echo "❌ sshpass is required for password authentication"
            echo "Install with: brew install sshpass (macOS) or apt-get install sshpass (Linux)"
            exit 1
        fi
        sshpass -p "$PI_PASSWORD" scp -o StrictHostKeyChecking=no "$1" "$PI_USER@$PI_HOST:$2"
    fi
}

# Function to run SSH commands
ssh_cmd() {
    if [ -n "$PI_SSH_KEY" ]; then
        ssh -i "$PI_SSH_KEY" -o StrictHostKeyChecking=no "$PI_USER@$PI_HOST" "$1"
    else
        sshpass -p "$PI_PASSWORD" ssh -o StrictHostKeyChecking=no "$PI_USER@$PI_HOST" "$1"
    fi
}

# Test connection
echo "📡 Testing connection..."
if ! ssh_cmd "echo 'Connection successful'"; then
    echo "❌ Failed to connect to Pi"
    exit 1
fi

# Create directory
echo "📁 Creating deployment directory..."
ssh_cmd "mkdir -p $DEPLOY_DIR"

# Check if required files exist
required_files=(
    "spondy-backend-prod.tar.gz"
    "spondy-frontend-prod.tar.gz" 
    "postgres-15-alpine.tar.gz"
    "docker-compose.prod.yml"
    "production.env"
    "setup-pi.sh"
)

missing_files=()
for file in "${required_files[@]}"; do
    if [ ! -f "$file" ]; then
        missing_files+=("$file")
    fi
done

if [ ${#missing_files[@]} -ne 0 ]; then
    echo "❌ Missing required files:"
    for file in "${missing_files[@]}"; do
        echo "   - $file"
    done
    echo ""
    echo "Make sure to build Docker images first:"
    echo "  docker build -t spondy-backend:prod ./backend"
    echo "  docker build -t spondy-frontend:prod ./frontend"
    echo "  docker save spondy-backend:prod | gzip > spondy-backend-prod.tar.gz"
    echo "  docker save spondy-frontend:prod | gzip > spondy-frontend-prod.tar.gz"
    echo "  docker pull postgres:15-alpine && docker save postgres:15-alpine | gzip > postgres-15-alpine.tar.gz"
    exit 1
fi

echo "✅ All required files found"

echo ""
echo "📤 Transferring files..."

echo "  - Docker images (this may take a while)..."
scp_file "spondy-backend-prod.tar.gz" "$DEPLOY_DIR/"
scp_file "spondy-frontend-prod.tar.gz" "$DEPLOY_DIR/"
scp_file "postgres-15-alpine.tar.gz" "$DEPLOY_DIR/"

echo "  - Configuration files..."
scp_file "docker-compose.prod.yml" "$DEPLOY_DIR/"
scp_file "production.env" "$DEPLOY_DIR/"

echo "  - Setup script..."
scp_file "setup-pi.sh" "$DEPLOY_DIR/"
ssh_cmd "chmod +x $DEPLOY_DIR/setup-pi.sh"

echo ""
echo "🎉 File transfer complete!"
echo ""
echo "📋 Next steps:"
echo "  1. SSH into your Pi:"
echo "     ssh $PI_USER@$PI_HOST"
echo ""
echo "  2. Run the setup script:"
echo "     cd $DEPLOY_DIR"
echo "     ./setup-pi.sh"
echo ""
echo "  3. The script will:"
echo "     - Install Docker (if needed)"
echo "     - Load all Docker images"
echo "     - Start the application"
echo "     - Run health checks"
echo ""
echo "📊 After setup, your app will be available at:"
echo "  Frontend: http://$PI_HOST/"
echo "  Backend API: http://$PI_HOST/api/"
echo "" 