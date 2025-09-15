# 🔐 GitHub Secrets Configuration Guide

This guide shows how to configure GitHub secrets for your ThunderFat Backend deployment pipeline.

## Required Secrets

### Database Configuration
```
MYSQL_HOST=your-mysql-host.com
MYSQL_PORT=3306
MYSQL_DATABASE=thunderfatboot
MYSQL_USERNAME=your-username
MYSQL_PASSWORD=your-secure-password
```

### Redis Configuration (Optional)
```
REDIS_HOST=your-redis-host.com
REDIS_PORT=6379
REDIS_PASSWORD=your-redis-password
```

### NPM Registry (For SDK Publishing)
```
NPM_TOKEN=npm_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
NPM_REGISTRY=https://registry.npmjs.org/
```

### GitHub Token (For Releases)
```
GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

## How to Add Secrets

1. **Go to your GitHub repository**: https://github.com/bolaxxx/thunderfat-spring-boot-back
2. **Click "Settings"** (in the top navigation)
3. **Click "Secrets and variables"** → **"Actions"**
4. **Click "New repository secret"**
5. **Add each secret** with the exact name and value

## Production Database Setup

### Option 1: PlanetScale (Recommended)
```bash
# Free tier MySQL with serverless scaling
# Visit: https://planetscale.com/
# Get connection string and add to secrets
```

### Option 2: AWS RDS MySQL
```bash
# Create RDS MySQL instance
# Configure security groups
# Add endpoint to MYSQL_HOST secret
```

### Option 3: Google Cloud SQL
```bash
# Create Cloud SQL MySQL instance
# Configure authorized networks
# Add connection details to secrets
```

## Testing Secrets Configuration

After adding secrets, the GitHub Actions workflow will automatically use them when triggered by:
- Push to master
- Pull request
- Manual workflow dispatch

## Security Best Practices

✅ **Use strong passwords** (20+ characters)  
✅ **Enable database SSL** connections  
✅ **Restrict database access** to GitHub IPs only  
✅ **Rotate secrets regularly** (every 90 days)  
✅ **Use least privilege** database users  
❌ **Never commit secrets** to code  

## Verification Commands

Test your pipeline with:
```bash
# Trigger GitHub Actions workflow
git push origin master

# Check workflow status
# Visit: https://github.com/bolaxxx/thunderfat-spring-boot-back/actions
```

---

**Next Step**: Configure NPM registry for SDK publishing
