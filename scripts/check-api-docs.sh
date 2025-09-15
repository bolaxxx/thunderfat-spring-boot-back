#!/bin/bash

# check-api-docs.sh - Script to test the OpenAPI docs endpoint

set -e

APP_PORT=${1:-8080}
TIMEOUT=${2:-120}

echo "🚀 Starting API docs diagnostics..."

# Function to check if application is running
check_app_health() {
    curl -s -f "http://localhost:$APP_PORT/actuator/health" > /dev/null
}

# Function to stop application gracefully
cleanup() {
    if [ ! -z "$APP_PID" ]; then
        echo "🛑 Stopping Spring Boot application (PID: $APP_PID)..."
        kill $APP_PID 2>/dev/null || true
        wait $APP_PID 2>/dev/null || true
    fi
}

# Set up cleanup trap
trap cleanup EXIT

# Start Spring Boot application
echo "📦 Starting Spring Boot application on port $APP_PORT..."
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=$APP_PORT -Dlogging.level.org.springdoc=DEBUG -Dlogging.level.org.springframework.web=DEBUG" > spring-boot-output.log 2>&1 &
APP_PID=$!

echo "⏳ Waiting for application to be ready (timeout: ${TIMEOUT}s)..."

# Wait for application to be ready
START_TIME=$(date +%s)
while ! check_app_health; do
    CURRENT_TIME=$(date +%s)
    ELAPSED=$((CURRENT_TIME - START_TIME))
    
    if [ $ELAPSED -gt $TIMEOUT ]; then
        echo "❌ Application failed to start within $TIMEOUT seconds"
        echo "📄 Check spring-boot-output.log for details"
        exit 1
    fi
    
    echo "   ... waiting (${ELAPSED}s elapsed)"
    sleep 2
done

echo "✅ Application is ready!"

# Check available actuator endpoints
echo "🔍 Checking available actuator endpoints..."
curl -s "http://localhost:$APP_PORT/actuator" > actuator.json
echo "📄 Saved actuator endpoints to actuator.json"

# Try all potential OpenAPI endpoints
echo -e "\n🔍 Checking /v3/api-docs endpoint..."
curl -v "http://localhost:$APP_PORT/v3/api-docs" > v3-api-docs.txt 2>&1
echo -e "\n📄 Response saved to v3-api-docs.txt"

echo -e "\n🔍 Checking /v3/api-docs.json endpoint..."
curl -v "http://localhost:$APP_PORT/v3/api-docs.json" > v3-api-docs-json.txt 2>&1
echo -e "\n📄 Response saved to v3-api-docs-json.txt"

echo -e "\n🔍 Checking /api-docs endpoint..."
curl -v "http://localhost:$APP_PORT/api-docs" > api-docs.txt 2>&1
echo -e "\n📄 Response saved to api-docs.txt"

echo -e "\n🔍 Checking /swagger-resources endpoint..."
curl -v "http://localhost:$APP_PORT/swagger-resources" > swagger-resources.txt 2>&1
echo -e "\n📄 Response saved to swagger-resources.txt"

echo -e "\n🔍 Checking /swagger-ui/index.html endpoint..."
curl -v "http://localhost:$APP_PORT/swagger-ui/index.html" > swagger-ui.txt 2>&1
echo -e "\n📄 Response saved to swagger-ui.txt"

# Check app mappings through actuator if available
echo -e "\n🔍 Checking application mappings..."
curl -s "http://localhost:$APP_PORT/actuator/mappings" > mappings.json 2>/dev/null || echo "⚠️ Mappings endpoint not available"

echo -e "\n✅ Diagnostics completed. Check the output files for details."
echo "📋 Summary of files generated:"
echo "   - spring-boot-output.log - Spring Boot startup logs"
echo "   - v3-api-docs.txt - Response from /v3/api-docs endpoint"
echo "   - v3-api-docs-json.txt - Response from /v3/api-docs.json endpoint"
echo "   - api-docs.txt - Response from /api-docs endpoint"
echo "   - swagger-resources.txt - Response from /swagger-resources endpoint"
echo "   - swagger-ui.txt - Response from /swagger-ui/index.html endpoint"
echo "   - mappings.json - Application request mappings (if available)"

echo -e "\n📋 Possible solutions if API docs are not available:"
echo "   1. Check that springdoc-openapi-starter-webmvc-ui is in the classpath"
echo "   2. Verify springdoc.api-docs.path property in application.properties/yml"
echo "   3. Ensure no security filters are blocking access to /v3/api-docs"
echo "   4. Check for custom OpenAPI configuration beans in your code"
echo "   5. Look for any errors in spring-boot-output.log"
