#!/bin/bash

# export-api-spec.sh - Script to export OpenAPI specification from running application

set -e

APP_PORT=${1:-8080}
OUTPUT_FILE=${2:-api/openapi.json}
TIMEOUT=${3:-120}

echo "🚀 Starting API specification export..."

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
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=$APP_PORT" > /dev/null 2>&1 &
APP_PID=$!

echo "⏳ Waiting for application to be ready (timeout: ${TIMEOUT}s)..."

# Wait for application to be ready
START_TIME=$(date +%s)
while ! check_app_health; do
    CURRENT_TIME=$(date +%s)
    ELAPSED=$((CURRENT_TIME - START_TIME))
    
    if [ $ELAPSED -gt $TIMEOUT ]; then
        echo "❌ Application failed to start within $TIMEOUT seconds"
        exit 1
    fi
    
    echo "   ... waiting (${ELAPSED}s elapsed)"
    sleep 2
done

echo "✅ Application is ready!"

# Create api directory if it doesn't exist
mkdir -p "$(dirname "$OUTPUT_FILE")"

# Export OpenAPI specification
echo "📄 Exporting OpenAPI specification..."
if curl -s "http://localhost:$APP_PORT/v3/api-docs" > "$OUTPUT_FILE.tmp"; then
    # Validate JSON
    if python3 -m json.tool "$OUTPUT_FILE.tmp" > /dev/null 2>&1; then
        # Pretty format the JSON
        python3 -m json.tool "$OUTPUT_FILE.tmp" > "$OUTPUT_FILE"
        rm "$OUTPUT_FILE.tmp"
        echo "✅ OpenAPI specification exported to: $OUTPUT_FILE"
        
        # Show basic stats
        ENDPOINTS=$(jq -r '.paths | keys | length' "$OUTPUT_FILE" 2>/dev/null || echo "unknown")
        VERSION=$(jq -r '.info.version // "unknown"' "$OUTPUT_FILE" 2>/dev/null || echo "unknown")
        echo "📊 Specification stats:"
        echo "   - API Version: $VERSION"
        echo "   - Endpoints: $ENDPOINTS"
        echo "   - File size: $(du -h "$OUTPUT_FILE" | cut -f1)"
    else
        echo "❌ Invalid JSON response from API docs endpoint"
        exit 1
    fi
else
    echo "❌ Failed to fetch OpenAPI specification from application"
    exit 1
fi

echo "🎉 API specification export completed successfully!"
