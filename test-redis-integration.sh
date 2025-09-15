#!/bin/bash
# =====================================
# REDIS TESTING SCRIPT FOR THUNDERFAT
# =====================================
# This script tests Redis integration with your Spring Boot application

echo ""
echo "========================================="
echo "ThunderFat Redis Integration Test"
echo "========================================="
echo ""

# Check if Redis is available
echo "[INFO] Checking Redis availability..."
if redis-cli ping > /dev/null 2>&1; then
    echo "[SUCCESS] Redis is running and responding to ping!"
    
    # Test basic Redis operations
    echo "[TEST] Testing basic Redis operations..."
    redis-cli set thunderfat:test "Redis working!" > /dev/null
    TEST_VALUE=$(redis-cli get thunderfat:test)
    echo "[RESULT] Test value: $TEST_VALUE"
    
    # Clean up test key
    redis-cli del thunderfat:test > /dev/null
    
    echo ""
    echo "[INFO] Starting Spring Boot application with Redis caching..."
    echo "[INFO] The application will use Redis for caching when profile 'dev' is active"
    echo ""
    echo "To enable Redis caching, run your application with:"
    echo "  export SPRING_PROFILES_ACTIVE=dev,spanish-billing"
    echo "  ./mvnw spring-boot:run"
    echo ""
    echo "Or set the cache type directly:"
    echo "  export SPRING_CACHE_TYPE=redis"
    echo "  ./mvnw spring-boot:run"
    echo ""
    
    # Show current Redis info
    echo "[INFO] Current Redis information:"
    echo "  Version: $(redis-cli info server | grep redis_version | cut -d: -f2 | tr -d '\r')"
    echo "  Memory used: $(redis-cli info memory | grep used_memory_human | cut -d: -f2 | tr -d '\r')"
    echo "  Connected clients: $(redis-cli info clients | grep connected_clients | cut -d: -f2 | tr -d '\r')"
    
else
    echo "[WARNING] Redis is not running or not installed."
    echo ""
    echo "To install Redis:"
    echo ""
    echo "Windows (Chocolatey):"
    echo "  choco install redis-64"
    echo ""
    echo "Windows (Docker):"
    echo "  docker run -d -p 6379:6379 --name redis-thunderfat redis:alpine"
    echo ""
    echo "Linux/macOS:"
    echo "  sudo apt install redis-server   # Ubuntu/Debian"
    echo "  brew install redis              # macOS"
    echo ""
    echo "After installing Redis, run this script again to test the integration."
    echo ""
    echo "[INFO] The application will work without Redis using simple in-memory caching."
fi

echo ""
echo "========================================="
echo "Redis integration test completed"
echo "========================================="
