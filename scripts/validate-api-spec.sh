#!/bin/bash

# validate-api-spec.sh - Script to validate OpenAPI specification

set -e

SPEC_FILE=${1:-api/openapi.json}
PREVIOUS_SPEC=${2:-}
FAIL_ON_BREAKING=${3:-false}

echo "🔍 Starting API specification validation..."

# Check if spec file exists
if [ ! -f "$SPEC_FILE" ]; then
    echo "❌ OpenAPI specification file not found: $SPEC_FILE"
    exit 1
fi

# Install validation tools if not present
install_tools() {
    echo "📦 Installing validation tools..."
    
    if ! command -v spectral &> /dev/null; then
        npm install -g @stoplight/spectral-cli
    fi
    
    if ! command -v openapi-diff &> /dev/null; then
        npm install -g openapi-diff
    fi
}

# Create Spectral configuration if it doesn't exist
create_spectral_config() {
    if [ ! -f ".spectral.yml" ]; then
        echo "📝 Creating Spectral configuration..."
        cat > .spectral.yml << 'EOF'
extends: ["@stoplight/spectral/dist/rulesets/oas/index.js"]
rules:
  # Operation rules
  operation-operationId: error
  operation-description: warn
  operation-tag-defined: error
  operation-summary: warn
  
  # Parameter rules
  path-params: error
  no-unresolved-refs: error
  
  # Schema rules
  valid-example: warn
  
  # Info rules
  info-contact: off
  info-description: warn
  info-license: off
  
  # Disable some rules that might be too strict
  contact-properties: off
  license-url: off
  no-$ref-siblings: off
  
  # Custom rules for healthcare APIs
  paths-kebab-case: warn
  operation-4xx-response: warn
EOF
    fi
}

# Validate JSON format
validate_json() {
    echo "🔧 Validating JSON format..."
    if python3 -m json.tool "$SPEC_FILE" > /dev/null; then
        echo "✅ Valid JSON format"
    else
        echo "❌ Invalid JSON format"
        exit 1
    fi
}

# Run Spectral linting
run_spectral_lint() {
    echo "🔍 Running Spectral API linting..."
    
    # Create output directory
    mkdir -p reports
    
    # Run Spectral with multiple output formats
    if spectral lint "$SPEC_FILE" --format junit --output reports/spectral-junit.xml; then
        echo "✅ Spectral validation passed"
    else
        echo "⚠️  Spectral found issues (see details above)"
    fi
    
    # Also run with text format for immediate feedback
    echo ""
    echo "📋 Spectral summary:"
    spectral lint "$SPEC_FILE" --format text || true
}

# Check for breaking changes
check_breaking_changes() {
    if [ -n "$PREVIOUS_SPEC" ] && [ -f "$PREVIOUS_SPEC" ]; then
        echo "🔄 Checking for breaking changes..."
        
        # Create reports directory
        mkdir -p reports
        
        # Generate diff report
        if openapi-diff "$PREVIOUS_SPEC" "$SPEC_FILE" --format json > reports/api-diff.json; then
            # Check for breaking changes
            BREAKING_COUNT=$(jq -r '.breakingChanges | length' reports/api-diff.json 2>/dev/null || echo "0")
            NON_BREAKING_COUNT=$(jq -r '.nonBreakingChanges | length' reports/api-diff.json 2>/dev/null || echo "0")
            
            echo "📊 Change summary:"
            echo "   - Breaking changes: $BREAKING_COUNT"
            echo "   - Non-breaking changes: $NON_BREAKING_COUNT"
            
            if [ "$BREAKING_COUNT" -gt 0 ]; then
                echo "🚨 BREAKING CHANGES DETECTED:"
                jq -r '.breakingChanges[] | "  - \(.description // .message)"' reports/api-diff.json
                echo ""
                echo "💡 Recommendations:"
                echo "   1. Consider incrementing MAJOR version"
                echo "   2. Create a new API version (/api/v2)"
                echo "   3. Deprecate old endpoints before removing them"
                echo "   4. Update migration documentation"
                
                if [ "$FAIL_ON_BREAKING" = "true" ]; then
                    echo "❌ Failing due to breaking changes (FAIL_ON_BREAKING=true)"
                    exit 1
                fi
            else
                echo "✅ No breaking changes detected"
            fi
            
            if [ "$NON_BREAKING_COUNT" -gt 0 ]; then
                echo "📝 Non-breaking changes:"
                jq -r '.nonBreakingChanges[] | "  - \(.description // .message)"' reports/api-diff.json
            fi
        else
            echo "⚠️  Could not generate API diff report"
        fi
    else
        echo "ℹ️  No previous specification provided, skipping breaking change detection"
    fi
}

# Generate validation report
generate_report() {
    echo "📊 Generating validation report..."
    
    # Get basic API metrics
    API_VERSION=$(jq -r '.info.version // "unknown"' "$SPEC_FILE")
    API_TITLE=$(jq -r '.info.title // "unknown"' "$SPEC_FILE")
    ENDPOINT_COUNT=$(jq -r '.paths | keys | length' "$SPEC_FILE")
    SCHEMA_COUNT=$(jq -r '.components.schemas | keys | length' "$SPEC_FILE" 2>/dev/null || echo "0")
    
    cat > reports/validation-summary.md << EOF
# API Validation Report

## Specification Details
- **Title**: $API_TITLE
- **Version**: $API_VERSION
- **Endpoints**: $ENDPOINT_COUNT
- **Schemas**: $SCHEMA_COUNT
- **Validated**: $(date)

## Validation Results
- JSON Format: ✅ Valid
- Spectral Linting: See spectral-junit.xml for details
- Breaking Changes: See api-diff.json for details

## Files Generated
- \`spectral-junit.xml\`: Spectral validation results in JUnit format
- \`api-diff.json\`: API diff report (if previous spec provided)
- \`validation-summary.md\`: This summary report
EOF
    
    echo "✅ Validation report generated: reports/validation-summary.md"
}

# Main execution
main() {
    echo "🔍 Validating OpenAPI specification: $SPEC_FILE"
    
    install_tools
    create_spectral_config
    validate_json
    run_spectral_lint
    check_breaking_changes
    generate_report
    
    echo "🎉 API specification validation completed!"
    echo "📂 Reports available in: reports/"
}

# Run main function
main
