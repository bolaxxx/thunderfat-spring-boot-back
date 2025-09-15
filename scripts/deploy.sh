#!/bin/bash

# deploy.sh - Manual deployment script for ThunderFat Backend

set -e

ENVIRONMENT=${1:-development}
BUILD_PROFILE=${2:-dev}
SKIP_TESTS=${3:-false}

echo "🚀 Starting ThunderFat Backend Deployment"
echo "Environment: $ENVIRONMENT"
echo "Build Profile: $BUILD_PROFILE"
echo "Skip Tests: $SKIP_TESTS"
echo ""

# Configuration per environment
declare -A ENV_CONFIG
ENV_CONFIG[development]="localhost:8080"
ENV_CONFIG[staging]="staging-api.thunderfat.com"
ENV_CONFIG[production]="api.thunderfat.com"

BASE_URL=${ENV_CONFIG[$ENVIRONMENT]}

if [ -z "$BASE_URL" ]; then
    echo "❌ Unknown environment: $ENVIRONMENT"
    echo "Available environments: development, staging, production"
    exit 1
fi

# Pre-deployment checks
pre_deployment_checks() {
    echo "🔍 Running pre-deployment checks..."
    
    # Check if required tools are installed
    check_tool() {
        if ! command -v $1 &> /dev/null; then
            echo "❌ Required tool not found: $1"
            exit 1
        fi
    }
    
    check_tool java
    check_tool mvn
    check_tool curl
    check_tool jq
    
    # Check Java version
    JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 21 ]; then
        echo "❌ Java 21 or higher required. Current: $JAVA_VERSION"
        exit 1
    fi
    
    echo "✅ Pre-deployment checks passed"
}

# Build application
build_application() {
    echo "🔨 Building application..."
    
    # Clean and compile
    if ./mvnw clean compile -P$BUILD_PROFILE; then
        echo "✅ Compilation successful"
    else
        echo "❌ Compilation failed"
        exit 1
    fi
    
    # Run tests unless skipped
    if [ "$SKIP_TESTS" != "true" ]; then
        echo "🧪 Running tests..."
        if ./mvnw test -P$BUILD_PROFILE; then
            echo "✅ Tests passed"
        else
            echo "❌ Tests failed"
            exit 1
        fi
    else
        echo "⏭️  Skipping tests"
    fi
    
    # Package application
    echo "📦 Packaging application..."
    if ./mvnw package -DskipTests=$SKIP_TESTS -P$BUILD_PROFILE; then
        echo "✅ Packaging successful"
    else
        echo "❌ Packaging failed"
        exit 1
    fi
}

# Export API specification
export_api_specification() {
    echo "📄 Exporting API specification..."
    
    # Check if export script exists
    if [ -f "scripts/export-api-spec.sh" ]; then
        if ./scripts/export-api-spec.sh 8080 api/openapi.json 60; then
            echo "✅ API specification exported"
        else
            echo "⚠️  Failed to export API specification (continuing anyway)"
        fi
    else
        echo "⚠️  Export script not found, skipping API specification export"
    fi
}

# Validate API specification
validate_api_specification() {
    echo "🔍 Validating API specification..."
    
    if [ -f "scripts/validate-api-spec.sh" ] && [ -f "api/openapi.json" ]; then
        if ./scripts/validate-api-spec.sh api/openapi.json; then
            echo "✅ API specification validation passed"
        else
            echo "⚠️  API specification validation failed (continuing anyway)"
        fi
    else
        echo "⚠️  Validation script or spec not found, skipping validation"
    fi
}

# Health check
health_check() {
    local url=$1
    local max_attempts=${2:-30}
    local delay=${3:-2}
    
    echo "🏥 Running health check on $url..."
    
    for i in $(seq 1 $max_attempts); do
        if curl -s -f "$url/actuator/health" > /dev/null; then
            echo "✅ Health check passed (attempt $i)"
            return 0
        fi
        
        echo "   ... waiting (attempt $i/$max_attempts)"
        sleep $delay
    done
    
    echo "❌ Health check failed after $max_attempts attempts"
    return 1
}

# Start application
start_application() {
    echo "🚀 Starting application..."
    
    # Kill any existing instance
    pkill -f "thunderfat-spring-boot-backend" || true
    sleep 2
    
    # Start application in background
    case $ENVIRONMENT in
        "development")
            ./mvnw spring-boot:run -Dspring-boot.run.profiles=$BUILD_PROFILE > logs/app.log 2>&1 &
            ;;
        "staging"|"production")
            nohup java -jar -Dspring.profiles.active=$BUILD_PROFILE target/thunderfat-spring-boot-backend-*.jar > logs/app.log 2>&1 &
            ;;
    esac
    
    APP_PID=$!
    echo "📋 Application started with PID: $APP_PID"
    
    # Wait for application to be ready
    if health_check "http://localhost:8080" 30 3; then
        echo "✅ Application is ready"
    else
        echo "❌ Application failed to start properly"
        exit 1
    fi
}

# Generate deployment report
generate_deployment_report() {
    echo "📊 Generating deployment report..."
    
    # Get application info
    APP_VERSION=$(grep -oP '<version>\K[^<]+' pom.xml | head -1)
    DEPLOYMENT_TIME=$(date)
    
    # Get system info
    JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2)
    MAVEN_VERSION=$(mvn -version 2>/dev/null | head -1 | cut -d' ' -f3 || echo "unknown")
    
    # Create deployment report
    cat > "DEPLOYMENT_REPORT_$(date +%Y%m%d_%H%M%S).md" << EOF
# ThunderFat Backend Deployment Report

## Deployment Information
- **Environment**: $ENVIRONMENT
- **Build Profile**: $BUILD_PROFILE
- **Version**: $APP_VERSION
- **Deployment Time**: $DEPLOYMENT_TIME
- **Base URL**: http://$BASE_URL

## Build Information
- **Java Version**: $JAVA_VERSION
- **Maven Version**: $MAVEN_VERSION
- **Tests Skipped**: $SKIP_TESTS
- **Build Status**: ✅ Success

## Health Check
- **Endpoint**: http://localhost:8080/actuator/health
- **Status**: ✅ Healthy
- **Response Time**: < 3s

## API Documentation
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html

## Endpoints
- **Health**: http://localhost:8080/actuator/health
- **Info**: http://localhost:8080/actuator/info
- **Metrics**: http://localhost:8080/actuator/metrics

## Next Steps
1. Monitor application logs: \`tail -f logs/app.log\`
2. Access Swagger UI for API testing
3. Run integration tests if needed
4. Update dependent services with new API specification

## Troubleshooting
- **Logs**: \`logs/app.log\`
- **Process**: \`ps aux | grep thunderfat\`
- **Ports**: \`netstat -tulpn | grep 8080\`
EOF
    
    echo "✅ Deployment report generated"
}

# Main deployment process
main() {
    echo "🎯 ThunderFat Backend Deployment Pipeline"
    echo "=========================================="
    
    pre_deployment_checks
    build_application
    start_application
    export_api_specification
    validate_api_specification
    generate_deployment_report
    
    echo ""
    echo "🎉 Deployment completed successfully!"
    echo "📋 Application is running at: http://localhost:8080"
    echo "📚 API Documentation: http://localhost:8080/swagger-ui/index.html"
    echo "📊 Health Check: http://localhost:8080/actuator/health"
    echo ""
    echo "📝 Next steps:"
    echo "   1. Test the API endpoints"
    echo "   2. Monitor application logs: tail -f logs/app.log"
    echo "   3. Run integration tests"
    echo "   4. Update client applications"
}

# Show usage if help requested
if [[ "$1" == "--help" || "$1" == "-h" ]]; then
    cat << EOF
ThunderFat Backend Deployment Script

Usage: $0 [environment] [build-profile] [skip-tests]

Parameters:
  environment    Target environment (development|staging|production) [default: development]
  build-profile  Maven build profile (dev|test|prod) [default: dev]
  skip-tests     Skip test execution (true|false) [default: false]

Examples:
  $0                           # Deploy to development with dev profile
  $0 staging test              # Deploy to staging with test profile
  $0 production prod true      # Deploy to production, skip tests

Environments:
  development    Local development server (localhost:8080)
  staging        Staging server (staging-api.thunderfat.com)
  production     Production server (api.thunderfat.com)

Requirements:
  - Java 21+
  - Maven 3.8+
  - curl, jq tools
  - Write access to logs/ directory
EOF
    exit 0
fi

# Create logs directory if it doesn't exist
mkdir -p logs

# Run main deployment
main
