# Redis Caching Configuration for ThunderFat

## Overview

This document explains how to set up and use Redis caching in the ThunderFat Spring Boot backend for enhanced performance in development and production environments.

## 🚀 Quick Setup

### Option 1: Using Setup Script (Recommended)
```bash
# Run the automated setup script
./setup-redis.bat
```

### Option 2: Manual Installation

#### Windows (Chocolatey)
```bash
choco install redis-64
redis-server
```

#### Windows (Docker)
```bash
docker run -d -p 6379:6379 --name redis-thunderfat redis:alpine
```

#### Linux/WSL
```bash
sudo apt update
sudo apt install redis-server
sudo service redis-server start
```

## ⚙️ Configuration

### Enable Redis Caching

#### Method 1: Using Development Profile
```properties
# In application.properties or as environment variable
spring.profiles.active=dev,spanish-billing
```

#### Method 2: Direct Configuration
```properties
spring.cache.type=redis
```

### Redis Connection Settings

The application uses these default Redis settings (configured in `application-dev.yml`):

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 2
          max-wait: 2000ms
```

## 🎯 Cache Regions & TTL Settings

### Patient Management
- `pacientes`: 30 minutes
- `pacientes-by-nutritionist`: 45 minutes
- `paciente-stats`: 15 minutes
- `paciente-search-*`: 10-60 minutes (varies by type)

### Appointment System
- `citas-*`: 15-20 minutes
- `next-appointment`: 20 minutes
- `appointment-conflicts`: 5 minutes
- `calendar-events`: 10 minutes

### Core Entities
- `alimentos`: 4 hours (rarely changes)
- `nutricionistas`: 2 hours
- `roles`: 24 hours (very stable)
- `statistics`: 15 minutes (calculated values)

### Nutrition Planning
- `platos-predeterminados`: 2 hours
- `comidas`: 30 minutes
- `comida-substitutions`: 2 hours

### Spanish Billing (Compliance)
- `facturas`: 30 minutes
- `iva-calculations`: 4 hours
- `verifactu-responses`: 24 hours
- `certificados-cache`: 12 hours

## 🔧 Development Tools

### Redis Monitor
```bash
# Run the monitoring tool
./redis-monitor.bat
```

Features:
- Real-time cache activity monitoring
- Cache statistics and performance metrics
- Key management and cleanup
- Performance testing

### Useful Redis Commands

```bash
# Check Redis status
redis-cli ping

# Monitor all commands
redis-cli monitor

# View ThunderFat cache keys
redis-cli keys thunderfat:*

# Clear all ThunderFat caches
redis-cli eval "for _,k in ipairs(redis.call('keys','thunderfat:*')) do redis.call('del',k) end" 0

# View memory usage
redis-cli info memory

# View cache hit/miss statistics
redis-cli info stats
```

## 🏗️ Architecture

### Cache Manager Configuration

The application uses a sophisticated Redis cache manager with:

- **JSON Serialization**: Complex objects are stored as JSON
- **String Keys**: Human-readable cache keys with `thunderfat:` prefix
- **Custom TTL**: Different cache regions have optimized TTL values
- **Transaction Support**: Cache operations respect `@Transactional` boundaries

### Fallback Behavior

When Redis is unavailable:
- Application continues to work with simple in-memory caching
- Health check shows Redis as DOWN but application stays UP
- Automatic fallback to `ConcurrentMapCacheManager`

## 📊 Performance Benefits

### Expected Cache Hit Rates
- **Patient Data**: 70-80% (frequently accessed)
- **Food Database**: 90-95% (rarely changes)
- **Nutritionist Data**: 85-90% (stable data)
- **Appointment Queries**: 60-70% (time-sensitive)

### Memory Usage
- Typical development environment: 50-100MB
- Production environment: 200-500MB (depending on users)

## 🛠️ Troubleshooting

### Redis Not Starting
```bash
# Check if Redis is running
redis-cli ping

# Start Redis manually
redis-server

# Check Redis logs
redis-cli info log
```

### Application Not Using Redis
1. Verify `spring.cache.type=redis` is set
2. Check Redis connectivity: `redis-cli ping`
3. Review application logs for cache configuration
4. Ensure `dev` profile is active

### Memory Issues
```bash
# Check Redis memory usage
redis-cli info memory

# Clear specific cache patterns
redis-cli eval "for _,k in ipairs(redis.call('keys','thunderfat:paciente*')) do redis.call('del',k) end" 0

# Set memory limit (if needed)
redis-cli config set maxmemory 256mb
redis-cli config set maxmemory-policy allkeys-lru
```

## 🚀 Production Considerations

### Security
- Configure Redis authentication: `requirepass`
- Use TLS encryption for Redis connections
- Restrict Redis network access
- Regular security updates

### Monitoring
- Set up Redis monitoring (Prometheus + Grafana)
- Monitor cache hit rates and memory usage
- Alert on Redis availability issues
- Track application performance metrics

### Scaling
- Consider Redis Cluster for high availability
- Implement Redis Sentinel for automatic failover
- Use Redis persistence (RDB + AOF) for data durability
- Plan for Redis backup and recovery procedures

## 📝 Configuration Examples

### Local Development
```yaml
spring:
  profiles:
    active: dev,spanish-billing
  cache:
    type: redis
```

### Docker Compose
```yaml
version: '3.8'
services:
  redis:
    image: redis:alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    command: redis-server --appendonly yes

volumes:
  redis_data:
```

### Production (with authentication)
```yaml
spring:
  data:
    redis:
      host: redis-cluster.example.com
      port: 6379
      password: ${REDIS_PASSWORD}
      ssl: true
      timeout: 5000ms
```

## 🎯 Best Practices

1. **Cache Key Design**: Use meaningful, hierarchical keys
2. **TTL Strategy**: Set appropriate TTL values based on data volatility
3. **Memory Management**: Monitor Redis memory usage regularly
4. **Error Handling**: Implement graceful degradation when Redis is unavailable
5. **Testing**: Test both with and without Redis in your test suites
6. **Monitoring**: Set up comprehensive monitoring and alerting

---

For more information, see the [ThunderFat Caching Strategy](./CACHING_STRATEGY.md) documentation.
