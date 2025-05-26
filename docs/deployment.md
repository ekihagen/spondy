# Production Deployment Guide

Complete guide for deploying Spondy to production on Raspberry Pi.

## 🎯 Overview

Spondy is deployed using a professional Docker + nginx reverse proxy architecture:
- **Host nginx** handles SSL/TLS termination and routing
- **Docker containers** run on localhost for security
- **Automated deployment** via shell scripts

**Live Production**: https://spondy.rotchess.com

## 🏗️ Architecture

```
Internet (HTTPS)
     ↓
┌─────────────────────────────────────┐
│     Raspberry Pi (192.168.68.60)   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │      Host Nginx             │   │
│  │      Port 80/443            │   │
│  │      SSL Termination        │   │
│  └─────────────┬───────────────┘   │
│                │                   │
│  ┌─────────────▼───────────────┐   │
│  │     Docker Network          │   │
│  │                             │   │
│  │  Frontend    Backend        │   │
│  │  :81         :8081          │   │
│  │     │           │           │   │
│  │     └───────────┼───────┐   │   │
│  │                 │       │   │   │
│  │            PostgreSQL   │   │   │
│  │               :5433     │   │   │
│  └─────────────────────────┼───┘   │
│                            │       │
│  localhost only (127.0.0.1)│       │
└────────────────────────────┼───────┘
                             │
                    External access
                    via nginx proxy
```

## 🔧 Port Configuration

| Service | Container Port | Host Binding | External Access |
|---------|---------------|--------------|-----------------|
| **Host Nginx** | - | 80/443 | ✅ Public (HTTPS) |
| **Frontend** | 8080 | 127.0.0.1:81 | ❌ Localhost only |
| **Backend** | 8080 | 127.0.0.1:8081 | ❌ Localhost only |
| **PostgreSQL** | 5432 | 127.0.0.1:5433 | ❌ Localhost only |

## 🚀 Quick Deployment

### Prerequisites
- MacBook with Docker installed
- Raspberry Pi with SSH access
- Domain name configured (optional)

### 1. Build and Deploy
```bash
# On MacBook - build production images
docker build -t spondy-backend:prod ./backend
docker build -t spondy-frontend:prod ./frontend

# Deploy to Pi (automated)
./deploy-to-pi.sh
```

### 2. Setup Nginx (First time only)
```bash
# On Raspberry Pi
sudo cp ~/spondy-prod/spondy-nginx-subdomain.conf /etc/nginx/sites-available/spondy
sudo ln -s /etc/nginx/sites-available/spondy /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### 3. Setup SSL (First time only)
```bash
# On Raspberry Pi
sudo certbot --nginx -d spondy.rotchess.com
```

## 📁 Key Files

### Docker Configuration
- `docker-compose.prod.yml` - Production Docker setup
- `production.env` - Environment variables
- `backend/Dockerfile` - Multi-stage backend build
- `frontend/Dockerfile` - Multi-stage frontend build

### Nginx Configuration
- `spondy-nginx-subdomain.conf` - nginx site configuration
- `frontend/nginx.conf` - Container nginx for API proxy

### Deployment Scripts
- `deploy-to-pi.sh` - Complete deployment automation
- `setup-pi.sh` - Docker installation for Pi

## 🔒 Security Features

1. **Container Isolation**
   - All containers bind to localhost only
   - No direct external access to containers

2. **SSL/TLS**
   - Let's Encrypt certificates
   - Automatic HTTPS redirect
   - Modern TLS configuration

3. **Security Headers**
   - XSS protection
   - CSRF protection
   - Clickjacking prevention

4. **Non-root Execution**
   - All containers run as non-root users
   - Minimal privileges

## 🛠️ Management Commands

### Container Management
```bash
# On Raspberry Pi
cd ~/spondy-prod

# View status
docker-compose -f docker-compose.prod.yml ps

# View logs
docker-compose -f docker-compose.prod.yml logs -f

# Restart services
docker-compose -f docker-compose.prod.yml restart

# Stop services
docker-compose -f docker-compose.prod.yml down

# Start services
docker-compose -f docker-compose.prod.yml up -d
```

### Nginx Management
```bash
# Test configuration
sudo nginx -t

# Reload configuration
sudo systemctl reload nginx

# View logs
sudo tail -f /var/log/nginx/error.log
sudo tail -f /var/log/nginx/access.log
```

### SSL Certificate Renewal
```bash
# Certificates auto-renew, but to test:
sudo certbot renew --dry-run
```

## 🔍 Health Checks

### Application Health
- **Frontend**: https://spondy.rotchess.com/
- **Backend API**: https://spondy.rotchess.com/api/form
- **Backend Health**: https://spondy.rotchess.com/api/actuator/health

### Container Health
```bash
# Check all containers
docker-compose -f docker-compose.prod.yml ps

# Check specific service
docker-compose -f docker-compose.prod.yml exec backend curl http://localhost:8080/actuator/health
```

## 🔄 Updates

### Application Updates
```bash
# On MacBook - rebuild and redeploy
docker build -t spondy-backend:prod ./backend
docker build -t spondy-frontend:prod ./frontend
./deploy-to-pi.sh
```

### System Updates
```bash
# On Raspberry Pi
sudo apt update && sudo apt upgrade -y
sudo reboot  # if kernel updates
```

## 🐛 Troubleshooting

### Common Issues

**1. Containers not starting**
```bash
# Check logs
docker-compose -f docker-compose.prod.yml logs

# Check disk space
df -h

# Check memory
free -h
```

**2. Nginx errors**
```bash
# Check configuration
sudo nginx -t

# Check if ports are in use
sudo netstat -tlnp | grep :80
sudo netstat -tlnp | grep :443
```

**3. SSL certificate issues**
```bash
# Check certificate status
sudo certbot certificates

# Renew if needed
sudo certbot renew
```

**4. Database connection issues**
```bash
# Check PostgreSQL container
docker-compose -f docker-compose.prod.yml logs postgres

# Check if database is accessible
docker-compose -f docker-compose.prod.yml exec postgres psql -U spondy_user -d spondy_db -c "SELECT 1;"
```

### Log Locations
- **Nginx**: `/var/log/nginx/`
- **Docker containers**: `docker-compose logs`
- **System**: `journalctl -u docker`

## 📊 Monitoring

### Performance Monitoring
```bash
# Container resource usage
docker stats

# System resources
htop
iotop
```

### Uptime Monitoring
Consider setting up external monitoring for:
- https://spondy.rotchess.com/
- https://spondy.rotchess.com/api/actuator/health

## 🔮 Future Improvements

### Planned Enhancements
- [ ] Automated backups for PostgreSQL
- [ ] Log aggregation (ELK stack)
- [ ] Prometheus/Grafana monitoring
- [ ] Blue-green deployments
- [ ] Container image scanning

### Scaling Options
- Load balancer for multiple backend instances
- Database read replicas
- CDN for static assets
- Container orchestration (Kubernetes)

## 📞 Support

For deployment issues:
1. Check the troubleshooting section above
2. Review container and nginx logs
3. Verify network connectivity and DNS
4. Check system resources (disk, memory, CPU) 