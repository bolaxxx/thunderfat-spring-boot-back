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
echo "🔍 Trying to fetch API docs from: http://localhost:$APP_PORT/v3/api-docs"

# Direct the curl output directly to the temp file
# Removing verbose mode which was causing issues with JSON parsing
curl -s "http://localhost:$APP_PORT/v3/api-docs" > "$OUTPUT_FILE.tmp"
CURL_STATUS=$?

if [ $CURL_STATUS -ne 0 ] || [ ! -s "$OUTPUT_FILE.tmp" ]; then
    echo "⚠️ Primary endpoint failed, trying alternative endpoints..."
    
    # Try v3/api-docs.json
    echo "🔍 Trying endpoint: http://localhost:$APP_PORT/v3/api-docs.json"
    curl -s "http://localhost:$APP_PORT/v3/api-docs.json" > "$OUTPUT_FILE.tmp"
    CURL_STATUS=$?
    
    # If still fails, try api-docs endpoint
    if [ $CURL_STATUS -ne 0 ] || [ ! -s "$OUTPUT_FILE.tmp" ]; then
        echo "🔍 Trying endpoint: http://localhost:$APP_PORT/api-docs"
        curl -s "http://localhost:$APP_PORT/api-docs" > "$OUTPUT_FILE.tmp"
        CURL_STATUS=$?
    fi
    
    # If still fails, try swagger endpoint
    if [ $CURL_STATUS -ne 0 ] || [ ! -s "$OUTPUT_FILE.tmp" ]; then
        echo "🔍 Trying endpoint: http://localhost:$APP_PORT/swagger-resources"
        curl -s "http://localhost:$APP_PORT/swagger-resources" > "$OUTPUT_FILE.tmp"
        CURL_STATUS=$?
    fi
fi

# Check if we actually have JSON content
if [ -s "$OUTPUT_FILE.tmp" ]; then
    # Save the first 100 characters to check if it's valid JSON
    head -c 100 "$OUTPUT_FILE.tmp" > "json_check.txt"
    if grep -q "^{" "json_check.txt"; then
        echo "✅ Successfully fetched API docs"
    else
        echo "⚠️ Response may not be valid JSON. First 100 characters:"
        cat "json_check.txt"
    fi
    rm "json_check.txt"
else
    echo "❌ Failed to fetch API docs - empty response"
    exit 1
fi

# Try to validate and format the JSON
# First attempt with native JSON.parse in JavaScript
if [ -s "$OUTPUT_FILE.tmp" ]; then
    if command -v node &> /dev/null; then
        # Use Node.js to validate and format the JSON
        if node -e "JSON.parse(require('fs').readFileSync('$OUTPUT_FILE.tmp', 'utf8')); process.exit(0);" 2>/dev/null; then
            # Format with Node
            node -e "const fs=require('fs'); const content=fs.readFileSync('$OUTPUT_FILE.tmp', 'utf8'); fs.writeFileSync('$OUTPUT_FILE', JSON.stringify(JSON.parse(content), null, 2));" 2>/dev/null
            echo "✅ OpenAPI specification exported to: $OUTPUT_FILE"
            rm "$OUTPUT_FILE.tmp"
        else
            # If Node validation fails, try simply copying the file
            cp "$OUTPUT_FILE.tmp" "$OUTPUT_FILE"
            echo "⚠️ JSON validation failed but file was saved"
            echo "📄 Manual inspection recommended"
            rm "$OUTPUT_FILE.tmp"
        fi
    else
        # If Node.js is not available, just copy the file as is
        cp "$OUTPUT_FILE.tmp" "$OUTPUT_FILE"
        echo "⚠️ JSON tools not available, saving raw output"
        rm "$OUTPUT_FILE.tmp"
    fi
    
    # Show basic file stats
    echo "📊 Specification stats:"
    echo "   - File size: $(du -h "$OUTPUT_FILE" | cut -f1 2>/dev/null || stat -c %s "$OUTPUT_FILE" 2>/dev/null || echo "unknown")"
    echo "✅ OpenAPI specification exported to: $OUTPUT_FILE"
else
    echo "❌ Failed to fetch OpenAPI specification from application"
    exit 1
fi

echo "🎉 API specification export completed successfully!"
