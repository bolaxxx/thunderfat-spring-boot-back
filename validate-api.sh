#!/bin/bash

# Quick API validation script for development
# Runs basic tests against the running API

set -e

echo "🧪 Running ThunderFat API Tests"

BASE_URL=${1:-"http://localhost:8080"}
API_BASE="$BASE_URL/api/v1"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Helper functions
log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Test health endpoint
echo "🏥 Testing health endpoint..."
if curl -f -s "$BASE_URL/actuator/health" > /dev/null; then
    log_success "Health endpoint is accessible"
else
    log_error "Health endpoint is not accessible"
    exit 1
fi

# Test OpenAPI docs
echo "📋 Testing OpenAPI documentation..."
if curl -f -s "$BASE_URL/v3/api-docs" > /dev/null; then
    log_success "OpenAPI docs are accessible"
else
    log_error "OpenAPI docs are not accessible"
    exit 1
fi

# Test Swagger UI
echo "🎨 Testing Swagger UI..."
if curl -f -s "$BASE_URL/swagger-ui/index.html" > /dev/null; then
    log_success "Swagger UI is accessible"
else
    log_warning "Swagger UI might not be accessible"
fi

# Test CORS headers
echo "🌐 Testing CORS configuration..."
CORS_RESPONSE=$(curl -s -I -X OPTIONS \
    -H "Origin: http://localhost:4200" \
    -H "Access-Control-Request-Method: GET" \
    -H "Access-Control-Request-Headers: Authorization" \
    "$API_BASE/patients" || echo "")

if echo "$CORS_RESPONSE" | grep -i "access-control-allow-origin" > /dev/null; then
    log_success "CORS headers are present"
else
    log_warning "CORS headers might not be configured properly"
fi

# Test security headers
echo "🔒 Testing security headers..."
SECURITY_RESPONSE=$(curl -s -I "$BASE_URL/" || echo "")

check_header() {
    local header=$1
    local description=$2
    
    if echo "$SECURITY_RESPONSE" | grep -i "$header" > /dev/null; then
        log_success "$description header is present"
    else
        log_warning "$description header is missing"
    fi
}

check_header "x-content-type-options" "X-Content-Type-Options"
check_header "x-frame-options" "X-Frame-Options"
check_header "x-xss-protection" "X-XSS-Protection"
check_header "strict-transport-security" "HSTS"

# Test authentication endpoint
echo "🔑 Testing authentication endpoint..."
AUTH_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -d '{"username":"invalid","password":"invalid"}' \
    "$BASE_URL/auth/login" || echo "000")

if [ "$AUTH_RESPONSE" = "401" ] || [ "$AUTH_RESPONSE" = "400" ]; then
    log_success "Authentication endpoint responds correctly to invalid credentials"
elif [ "$AUTH_RESPONSE" = "404" ]; then
    log_warning "Authentication endpoint might not be configured (404)"
else
    log_warning "Authentication endpoint returned unexpected status: $AUTH_RESPONSE"
fi

# Test rate limiting (if implemented)
echo "⚡ Testing rate limiting..."
RATE_LIMIT_STATUS=0
for i in {1..5}; do
    STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/actuator/health" || echo "000")
    if [ "$STATUS" = "429" ]; then
        RATE_LIMIT_STATUS=1
        break
    fi
done

if [ "$RATE_LIMIT_STATUS" = "1" ]; then
    log_success "Rate limiting is active"
else
    log_warning "Rate limiting might not be configured"
fi

# Test API versioning
echo "🔢 Testing API versioning..."
V1_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$API_BASE/patients" || echo "000")

if [ "$V1_STATUS" = "401" ] || [ "$V1_STATUS" = "403" ]; then
    log_success "API v1 endpoint exists (authentication required)"
elif [ "$V1_STATUS" = "200" ]; then
    log_success "API v1 endpoint is accessible"
else
    log_warning "API v1 endpoint might not be configured properly (status: $V1_STATUS)"
fi

# Test content negotiation
echo "🎭 Testing content negotiation..."
JSON_RESPONSE=$(curl -s -o /dev/null -w "%{content_type}" \
    -H "Accept: application/json" \
    "$BASE_URL/actuator/health" || echo "")

if echo "$JSON_RESPONSE" | grep -i "application/json" > /dev/null; then
    log_success "JSON content negotiation works"
else
    log_warning "JSON content negotiation might not work properly"
fi

# Test compression
echo "🗜️  Testing compression..."
COMPRESSION_RESPONSE=$(curl -s -I -H "Accept-Encoding: gzip" \
    "$BASE_URL/v3/api-docs" || echo "")

if echo "$COMPRESSION_RESPONSE" | grep -i "content-encoding.*gzip" > /dev/null; then
    log_success "Gzip compression is enabled"
else
    log_warning "Gzip compression might not be enabled"
fi

# Test metrics endpoint
echo "📊 Testing metrics endpoint..."
if curl -f -s "$BASE_URL/actuator/prometheus" > /dev/null; then
    log_success "Prometheus metrics are accessible"
else
    log_warning "Prometheus metrics might not be accessible"
fi

# Performance test (simple)
echo "⚡ Running simple performance test..."
START_TIME=$(date +%s%N)
for i in {1..10}; do
    curl -s "$BASE_URL/actuator/health" > /dev/null
done
END_TIME=$(date +%s%N)

DURATION=$(( (END_TIME - START_TIME) / 1000000 ))  # Convert to milliseconds
AVG_RESPONSE_TIME=$(( DURATION / 10 ))

if [ "$AVG_RESPONSE_TIME" -lt 100 ]; then
    log_success "Average response time: ${AVG_RESPONSE_TIME}ms (excellent)"
elif [ "$AVG_RESPONSE_TIME" -lt 500 ]; then
    log_success "Average response time: ${AVG_RESPONSE_TIME}ms (good)"
else
    log_warning "Average response time: ${AVG_RESPONSE_TIME}ms (could be improved)"
fi

echo ""
echo "🎉 API validation completed!"
echo "💡 For more comprehensive testing, consider running the full test suite with 'mvn test'"
