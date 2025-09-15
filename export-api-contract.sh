#!/bin/bash

# Legacy Contract-First CI/CD Pipeline Script for ThunderFat API
# This script is deprecated. Use the new scripts in scripts/ directory:
# - scripts/export-api-spec.sh
# - scripts/validate-api-spec.sh  
# - scripts/generate-sdk.sh
# - scripts/deploy.sh

echo "⚠️  DEPRECATED: This script is deprecated."
echo "🔄 Use the new deployment pipeline scripts:"
echo "   - scripts/export-api-spec.sh    # Export OpenAPI spec"
echo "   - scripts/validate-api-spec.sh  # Validate and lint spec"
echo "   - scripts/generate-sdk.sh       # Generate TypeScript SDK"
echo "   - scripts/deploy.sh             # Full deployment pipeline"
echo ""
echo "🚀 Running new export script instead..."

# Delegate to the new script
if [ -f "scripts/export-api-spec.sh" ]; then
    exec ./scripts/export-api-spec.sh "$@"
else
    echo "❌ New export script not found. Please use the GitHub Actions pipeline."
    exit 1
fi

# Start the application if not running
echo "📋 Checking if application is running..."
if ! curl -f -s "$API_DOCS_ENDPOINT" > /dev/null; then
    echo "🔧 Starting Spring Boot application..."
    ./mvnw spring-boot:run > app.log 2>&1 &
    APP_PID=$!
    
    # Wait for application to start
    echo "⏳ Waiting for application to start..."
    for i in {1..60}; do
        if curl -f -s "$API_DOCS_ENDPOINT" > /dev/null; then
            echo "✅ Application is running!"
            break
        fi
        if [ $i -eq 60 ]; then
            echo "❌ Application failed to start within 60 seconds"
            kill $APP_PID 2>/dev/null || true
            exit 1
        fi
        sleep 1
    done
else
    echo "✅ Application is already running"
fi

# Export OpenAPI spec
echo "📥 Exporting OpenAPI specification..."
curl -s "$API_DOCS_ENDPOINT" | jq '.' > "$SPEC_FILE"

if [ ! -s "$SPEC_FILE" ]; then
    echo "❌ Failed to export OpenAPI specification"
    kill $APP_PID 2>/dev/null || true
    exit 1
fi

echo "✅ OpenAPI spec exported to $SPEC_FILE"

# Convert to YAML for better readability
if command -v yq > /dev/null; then
    echo "📄 Converting to YAML format..."
    yq eval -P "$SPEC_FILE" > "$SPEC_YAML_FILE"
    echo "✅ YAML spec created at $SPEC_YAML_FILE"
fi

# Validate the spec with Spectral (if available)
if command -v spectral > /dev/null; then
    echo "🔍 Validating OpenAPI spec with Spectral..."
    spectral lint "$SPEC_FILE" --format json > "$OUTPUT_DIR/spectral-report.json" || true
    
    # Check for errors
    if [ -s "$OUTPUT_DIR/spectral-report.json" ]; then
        ERRORS=$(jq '[.[] | select(.severity == 0)] | length' "$OUTPUT_DIR/spectral-report.json")
        WARNINGS=$(jq '[.[] | select(.severity == 1)] | length' "$OUTPUT_DIR/spectral-report.json")
        
        echo "📊 Spectral Results: $ERRORS errors, $WARNINGS warnings"
        
        if [ "$ERRORS" -gt 0 ]; then
            echo "❌ Spectral validation failed with errors"
            jq '.[] | select(.severity == 0)' "$OUTPUT_DIR/spectral-report.json"
            exit 1
        fi
    fi
    
    echo "✅ Spectral validation passed"
else
    echo "⚠️  Spectral not found, skipping validation"
    echo "   Install with: npm install -g @stoplight/spectral-cli"
fi

# Check for breaking changes (if previous spec exists)
PREVIOUS_SPEC="$OUTPUT_DIR/openapi-previous.json"
if [ -f "$PREVIOUS_SPEC" ]; then
    echo "🔄 Checking for breaking changes..."
    
    if command -v openapi-diff > /dev/null; then
        openapi-diff "$PREVIOUS_SPEC" "$SPEC_FILE" --format json > "$OUTPUT_DIR/diff-report.json" || true
        
        # Check for breaking changes
        if [ -s "$OUTPUT_DIR/diff-report.json" ]; then
            BREAKING_CHANGES=$(jq '.breakingChanges | length' "$OUTPUT_DIR/diff-report.json" 2>/dev/null || echo "0")
            
            if [ "$BREAKING_CHANGES" -gt 0 ]; then
                echo "⚠️  Breaking changes detected!"
                jq '.breakingChanges' "$OUTPUT_DIR/diff-report.json"
                echo "💡 Consider introducing v2 API or adding deprecation notices"
            else
                echo "✅ No breaking changes detected"
            fi
        fi
    else
        echo "⚠️  openapi-diff not found, skipping breaking change detection"
        echo "   Install with: npm install -g openapi-diff"
    fi
fi

# Generate TypeScript SDK (if openapi-typescript-codegen is available)
if command -v openapi-typescript-codegen > /dev/null; then
    echo "🔧 Generating TypeScript SDK..."
    SDK_DIR="./sdk/typescript"
    mkdir -p "$SDK_DIR"
    
    openapi-typescript-codegen \
        --input "$SPEC_FILE" \
        --output "$SDK_DIR" \
        --client fetch \
        --name ThunderFatAPI \
        --useOptions \
        --useUnionTypes
    
    echo "✅ TypeScript SDK generated in $SDK_DIR"
    
    # Update package.json version based on API changes
    if [ -f "$SDK_DIR/package.json" ]; then
        # Simple version bump logic - this should be more sophisticated in production
        CURRENT_VERSION=$(jq -r '.version' "$SDK_DIR/package.json" 2>/dev/null || echo "1.0.0")
        NEW_VERSION="1.0.$(date +%s)"  # Timestamp-based versioning for now
        
        jq ".version = \"$NEW_VERSION\"" "$SDK_DIR/package.json" > "$SDK_DIR/package.json.tmp"
        mv "$SDK_DIR/package.json.tmp" "$SDK_DIR/package.json"
        
        echo "🔢 SDK version updated to $NEW_VERSION"
    fi
else
    echo "⚠️  openapi-typescript-codegen not found, skipping SDK generation"
    echo "   Install with: npm install -g openapi-typescript-codegen"
fi

# Backup current spec for next run
cp "$SPEC_FILE" "$PREVIOUS_SPEC"

# Generate API documentation summary
echo "📚 Generating API documentation summary..."
cat > "$OUTPUT_DIR/api-summary.md" << EOF
# ThunderFat API Summary

Generated on: $(date)

## Endpoints Summary

$(jq -r '.paths | to_entries | map("- " + .key + " (" + (.value | keys | join(", ") | ascii_upcase) + ")") | .[]' "$SPEC_FILE")

## Models

$(jq -r '.components.schemas | keys | map("- " + .) | .[]' "$SPEC_FILE" 2>/dev/null || echo "No models found")

## Security Schemes

$(jq -r '.components.securitySchemes | keys | map("- " + .) | .[]' "$SPEC_FILE" 2>/dev/null || echo "No security schemes found")

EOF

# Update changelog if this is a release
if [ "$1" = "--release" ]; then
    echo "📝 Updating API changelog..."
    
    if [ ! -f "$CHANGELOG_FILE" ]; then
        cat > "$CHANGELOG_FILE" << EOF
# ThunderFat API Changelog

All notable changes to the ThunderFat API will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

EOF
    fi
    
    # Add new version entry (this should be automated based on git tags)
    VERSION=${2:-"v1.0.0"}
    DATE=$(date +%Y-%m-%d)
    
    # Insert new version after the header
    sed -i "8i\\
\\
## [$VERSION] - $DATE\\
\\
### Added\\
- API contract updates\\
\\
### Changed\\
- Updated OpenAPI specification\\
\\
### Fixed\\
- Various improvements and bug fixes\\
" "$CHANGELOG_FILE"
    
    echo "✅ Changelog updated for version $VERSION"
fi

# Stop the application if we started it
if [ ! -z "$APP_PID" ]; then
    echo "🛑 Stopping application..."
    kill $APP_PID 2>/dev/null || true
    wait $APP_PID 2>/dev/null || true
fi

echo "🎉 Contract pipeline completed successfully!"
echo "📁 Output files:"
echo "   - OpenAPI JSON: $SPEC_FILE"
[ -f "$SPEC_YAML_FILE" ] && echo "   - OpenAPI YAML: $SPEC_YAML_FILE"
[ -f "$OUTPUT_DIR/spectral-report.json" ] && echo "   - Spectral Report: $OUTPUT_DIR/spectral-report.json"
[ -f "$OUTPUT_DIR/diff-report.json" ] && echo "   - Diff Report: $OUTPUT_DIR/diff-report.json"
[ -d "./sdk/typescript" ] && echo "   - TypeScript SDK: ./sdk/typescript"
echo "   - API Summary: $OUTPUT_DIR/api-summary.md"
