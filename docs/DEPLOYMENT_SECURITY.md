# Deployment Security Guide

This guide explains how to securely deploy Spondy to your Raspberry Pi without exposing sensitive credentials in your repository.

## 🔒 Security Overview

The original `deploy-to-pi.sh` script contained hardcoded credentials, which is a security risk. We've created a secure alternative that uses external configuration files.

## 🚀 Quick Setup

### Option 1: Using Configuration File (Recommended)

1. **Copy the template:**
   ```bash
   cp deploy.config.template deploy.config
   ```

2. **Edit your configuration:**
   ```bash
   nano deploy.config  # or use your preferred editor
   ```

3. **Fill in your actual values:**
   ```bash
   PI_HOST="192.168.1.100"          # Your Pi's IP address
   PI_USER="your-username"          # SSH username
   PI_PASSWORD="your-password"      # SSH password
   DEPLOY_DIR="/home/your-username/spondy-prod"
   ```

4. **Deploy:**
   ```bash
   ./deploy-to-pi-secure.sh
   ```

### Option 2: Using Environment Variables

```bash
export PI_HOST="192.168.1.100"
export PI_USER="pi"
export PI_PASSWORD="your-password"
export DEPLOY_DIR="/home/pi/spondy-prod"
./deploy-to-pi-secure.sh
```

### Option 3: Using SSH Keys (Most Secure)

1. **Generate SSH key pair (if you don't have one):**
   ```bash
   ssh-keygen -t rsa -b 4096 -f ~/.ssh/pi_deploy_key
   ```

2. **Copy public key to Pi:**
   ```bash
   ssh-copy-id -i ~/.ssh/pi_deploy_key.pub your-username@192.168.1.100
   ```

3. **Configure deployment:**
   ```bash
   # In deploy.config
   PI_HOST="192.168.1.100"
   PI_USER="your-username"
   PI_SSH_KEY="$HOME/.ssh/pi_deploy_key"
   DEPLOY_DIR="/home/your-username/spondy-prod"
   # Remove or comment out PI_PASSWORD
   ```

4. **Deploy:**
   ```bash
   ./deploy-to-pi-secure.sh
   ```

## 📁 File Security

### Protected Files (Never Commit)
- `deploy.config` - Your actual deployment configuration
- `.env.deploy` - Alternative environment file
- `*.pem`, `*.key` - SSH private keys
- `production.env` - Production environment variables

### Safe to Commit
- `deploy.config.template` - Template with example values
- `deploy-to-pi-secure.sh` - Secure deployment script
- `DEPLOYMENT_SECURITY.md` - This documentation

## 🛡️ Security Best Practices

### 1. Use SSH Keys Instead of Passwords
SSH keys are more secure than passwords and can't be accidentally exposed in logs.

### 2. Restrict Network Access
Consider using a VPN or restricting SSH access to specific IP addresses on your Pi.

### 3. Regular Key Rotation
Rotate your SSH keys and passwords regularly.

### 4. Monitor Access Logs
Check your Pi's SSH logs regularly for unauthorized access attempts:
```bash
sudo tail -f /var/log/auth.log
```

### 5. Use Strong Passwords
If you must use password authentication, ensure it's strong and unique.

## 🔧 Troubleshooting

### "sshpass not found"
Install sshpass for password authentication:
```bash
# macOS
brew install sshpass

# Ubuntu/Debian
sudo apt-get install sshpass

# Or use SSH keys instead (recommended)
```

### "Permission denied"
1. Check your credentials in `deploy.config`
2. Verify the Pi's IP address is correct
3. Ensure SSH is enabled on the Pi
4. Try connecting manually: `ssh your-username@your-pi-ip`

### "Configuration file not found"
Make sure you've created either:
- `deploy.config` (copy from `deploy.config.template`)
- `.env.deploy` with your variables
- Or set environment variables directly

## 🔄 Migration from Old Script

If you were using the old `deploy-to-pi.sh`:

1. **Create your config:**
   ```bash
   cp deploy.config.template deploy.config
   # Edit deploy.config with your actual values
   ```

2. **Test the new script:**
   ```bash
   ./deploy-to-pi-secure.sh
   ```

3. **Remove the old script (optional):**
   ```bash
   # After confirming the new script works
   rm deploy-to-pi.sh
   ```

## 📞 Support

If you encounter issues:
1. Check this guide first
2. Verify your network connectivity to the Pi
3. Test SSH connection manually
4. Check the Pi's SSH configuration

Remember: **Never commit sensitive credentials to version control!** 