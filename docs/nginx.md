# Nginx Configuration Guide

Guide for configuring nginx as a reverse proxy for Spondy containers.

## 🎯 Overview

This guide shows how to configure your existing nginx server on the Raspberry Pi to proxy requests to the Spondy Docker containers, providing a professional production setup.

## 📋 Prerequisites

- Raspberry Pi with nginx already installed and running
- Docker and Docker Compose installed
- Spondy Docker containers deployed and running
- Domain name configured (optional but recommended)

## 🔧 Configuration Files

### Main Site Configuration

The main nginx configuration is in `spondy-nginx-subdomain.conf`:

```nginx
# Upstream definitions for load balancing and health checks
upstream spondy_backend {
    server 127.0.0.1:8081 max_fails=3 fail_timeout=30s;
}

upstream spondy_frontend {
    server 127.0.0.1:81 max_fails=3 fail_timeout=30s;
}

# Main server block for spondy.rotchess.com
server {
    listen 80;
    server_name spondy.rotchess.com;
    
    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;
    add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;
    
    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json;
    
    # API routes to backend
    location /api/ {
        proxy_pass http://spondy_backend/api/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }
    
    # All other routes to frontend
    location / {
        proxy_pass http://spondy_frontend/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }
}
```

### Container Nginx Configuration

The frontend container also runs nginx (`frontend/nginx.conf`) to handle API proxying:

```nginx
# Run as non-root user
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log notice;
pid /tmp/nginx.pid;

events {
    worker_connections 1024;
}

http {
    # Temporary directories for non-root user
    proxy_temp_path /tmp/proxy_temp;
    client_body_temp_path /tmp/client_temp;
    fastcgi_temp_path /tmp/fastcgi_temp;
    uwsgi_temp_path /tmp/uwsgi_temp;
    scgi_temp_path /tmp/scgi_temp;
    
    include /etc/nginx/mime.types;
    default_type application/octet-stream;
    
    # Logging
    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';
    access_log /var/log/nginx/access.log main;
    
    # Performance
    sendfile on;
    tcp_nopush on;
    keepalive_timeout 65;
    gzip on;
    
    server {
        listen 8080;
        server_name localhost;
        root /usr/share/nginx/html;
        index index.html;
        
        # Serve React app
        location / {
            try_files $uri $uri/ /index.html;
        }
        
        # Proxy API calls to backend
        location /api/ {
            proxy_pass http://backend:8080/api/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
        
        # Health check endpoint
        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }
    }
}
```

## 🚀 Setup Process

### 1. Deploy Spondy Containers

First, ensure your Spondy containers are running:

```bash
# On Raspberry Pi
cd ~/spondy-prod
docker-compose -f docker-compose.prod.yml up -d

# Verify containers are running
docker-compose -f docker-compose.prod.yml ps
```

This starts:
- **Backend**: `127.0.0.1:8081` (localhost only)
- **Frontend**: `127.0.0.1:81` (localhost only)
- **Database**: `127.0.0.1:5433` (localhost only)

### 2. Install Nginx Site Configuration

```bash
# Copy configuration to nginx sites-available
sudo cp ~/spondy-prod/spondy-nginx-subdomain.conf /etc/nginx/sites-available/spondy

# Enable the site
sudo ln -s /etc/nginx/sites-available/spondy /etc/nginx/sites-enabled/

# Test configuration
sudo nginx -t

# If test passes, reload nginx
sudo systemctl reload nginx
```

### 3. Setup SSL/TLS (Recommended)

```bash
# Install certbot if not already installed
sudo apt install certbot python3-certbot-nginx

# Get SSL certificate for your domain
sudo certbot --nginx -d spondy.rotchess.com

# Certbot will automatically modify the nginx configuration to add SSL
```

## 🔍 Testing and Verification

### Test Container Connectivity

```bash
# Test backend directly
curl http://127.0.0.1:8081/api/actuator/health

# Test frontend directly
curl http://127.0.0.1:81/

# Should return JSON for backend, HTML for frontend
```

### Test Nginx Proxy

```bash
# Test through nginx (replace with your domain/IP)
curl http://spondy.rotchess.com/api/form
curl http://spondy.rotchess.com/

# With HTTPS after SSL setup
curl https://spondy.rotchess.com/api/form
curl https://spondy.rotchess.com/
```

### Check Nginx Status

```bash
# Check nginx service status
sudo systemctl status nginx

# Test configuration syntax
sudo nginx -t

# View nginx processes
ps aux | grep nginx
```

## 🛠️ Troubleshooting

### Common Issues

**1. 502 Bad Gateway**
```bash
# Check if containers are running
docker-compose -f docker-compose.prod.yml ps

# Check container logs
docker-compose -f docker-compose.prod.yml logs backend
docker-compose -f docker-compose.prod.yml logs frontend

# Check nginx error logs
sudo tail -f /var/log/nginx/error.log
```

**2. SSL Certificate Issues**
```bash
# Check certificate status
sudo certbot certificates

# Test certificate renewal
sudo certbot renew --dry-run

# Check SSL configuration
sudo nginx -t
```

**3. Port Conflicts**
```bash
# Check what's using port 80/443
sudo netstat -tlnp | grep :80
sudo netstat -tlnp | grep :443

# Check if nginx is running
sudo systemctl status nginx
```

**4. Permission Issues**
```bash
# Check nginx configuration file permissions
ls -la /etc/nginx/sites-available/spondy
ls -la /etc/nginx/sites-enabled/spondy

# Check nginx user
ps aux | grep nginx
```

### Log Locations

- **Nginx error log**: `/var/log/nginx/error.log`
- **Nginx access log**: `/var/log/nginx/access.log`
- **Container logs**: `docker-compose logs`
- **System logs**: `journalctl -u nginx`

## 🔧 Advanced Configuration

### Rate Limiting

Add to the server block for DDoS protection:

```nginx
# Rate limiting
limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;
limit_req_zone $binary_remote_addr zone=web:10m rate=30r/s;

server {
    # ... existing config ...
    
    location /api/ {
        limit_req zone=api burst=20 nodelay;
        # ... existing proxy config ...
    }
    
    location / {
        limit_req zone=web burst=50 nodelay;
        # ... existing proxy config ...
    }
}
```

### Custom Error Pages

```nginx
server {
    # ... existing config ...
    
    # Custom error pages
    error_page 404 /404.html;
    error_page 500 502 503 504 /50x.html;
    
    location = /404.html {
        root /var/www/html;
        internal;
    }
    
    location = /50x.html {
        root /var/www/html;
        internal;
    }
}
```

### Health Check Endpoint

```nginx
server {
    # ... existing config ...
    
    # Health check endpoint
    location /nginx-health {
        access_log off;
        return 200 "nginx healthy\n";
        add_header Content-Type text/plain;
    }
}
```

## 🔄 Maintenance

### Configuration Updates

```bash
# After modifying nginx configuration
sudo nginx -t
sudo systemctl reload nginx
```

### SSL Certificate Renewal

```bash
# Certificates auto-renew via cron, but to manually renew:
sudo certbot renew

# Check auto-renewal timer
sudo systemctl status certbot.timer
```

### Log Rotation

Nginx logs are automatically rotated by logrotate. Check configuration:

```bash
# View logrotate configuration for nginx
cat /etc/logrotate.d/nginx
```

## 📊 Monitoring

### Real-time Monitoring

```bash
# Watch nginx access logs
sudo tail -f /var/log/nginx/access.log

# Watch nginx error logs
sudo tail -f /var/log/nginx/error.log

# Monitor nginx processes
watch 'ps aux | grep nginx'
```

### Performance Metrics

```bash
# Check nginx status (if status module enabled)
curl http://localhost/nginx_status

# Check connection counts
ss -tuln | grep :80
ss -tuln | grep :443
```

This nginx configuration provides a robust, secure, and performant reverse proxy setup for your Spondy application. 