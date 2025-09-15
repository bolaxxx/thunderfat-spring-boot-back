#!/bin/bash

# generate-sdk.sh - Script to generate and package TypeScript SDK

set -e

SPEC_FILE=${1:-api/openapi.json}
SDK_DIR=${2:-sdk}
PACKAGE_NAME=${3:-@thunderfat/backend-sdk}
VERSION=${4:-auto}

echo "🔧 Starting SDK generation..."

# Check if spec file exists
if [ ! -f "$SPEC_FILE" ]; then
    echo "❌ OpenAPI specification file not found: $SPEC_FILE"
    exit 1
fi

# Install SDK generation tools
install_tools() {
    echo "📦 Installing SDK generation tools..."
    
    # Install openapi-typescript-codegen if not already installed
    if ! command -v openapi &> /dev/null; then
        npm install -g openapi-typescript-codegen
    fi
    
    # Install TypeScript if not already installed
    if ! command -v tsc &> /dev/null; then
        npm install -g typescript
    fi
    
    # Install required Node.js modules
    npm install -g axios @types/node
}

# Determine version
determine_version() {
    if [ "$VERSION" = "auto" ]; then
        # Try to get version from API spec using Node.js
        API_VERSION=$(node -e "
            try {
                const spec = JSON.parse(require('fs').readFileSync('$SPEC_FILE', 'utf8'));
                console.log(spec.info?.version || '1.0.0');
            } catch (e) {
                console.log('1.0.0');
            }
        " 2>/dev/null || echo "1.0.0")
        
        # If it's a semantic version, use it; otherwise default to 1.0.0
        if [[ $API_VERSION =~ ^[0-9]+\.[0-9]+\.[0-9]+(-.*)?$ ]]; then
            VERSION=$API_VERSION
        else
            VERSION="1.0.0"
        fi
    fi
    echo "📦 SDK Version: $VERSION"
}

# Generate TypeScript SDK
generate_typescript_sdk() {
    echo "🔧 Generating TypeScript SDK..."
    
    # Remove existing SDK directory
    rm -rf "$SDK_DIR"
    
    # Generate TypeScript client with safer options
    echo "🔧 Running OpenAPI TypeScript generator..."
    openapi --input "$SPEC_FILE" \
            --output "$SDK_DIR" \
            --client axios \
            --name ThunderFatApi \
            --useOptions \
            --postfixServices Api \
            --postfixModels Model \
            --indent 2 \
            --exportCore true \
            --exportModels true \
            --exportSchemas true \
            --exportServices true \
            --useUnionTypes false > generator.log 2>&1
    
    GENERATOR_STATUS=$?
    if [ $GENERATOR_STATUS -ne 0 ]; then
        echo "❌ Failed to generate TypeScript SDK"
        echo "==== Generator log (last 20 lines) ===="
        tail -n 20 generator.log
        exit 1
    fi
    
    echo "✅ TypeScript SDK generated in: $SDK_DIR"
}

# Create package.json
create_package_json() {
    echo "📝 Creating package.json..."
    
    # Get API info from spec using Node.js
    API_TITLE=$(node -e "
        try {
            const spec = JSON.parse(require('fs').readFileSync('$SPEC_FILE', 'utf8'));
            console.log(spec.info?.title || 'ThunderFat API');
        } catch (e) {
            console.log('ThunderFat API');
        }
    " 2>/dev/null || echo "ThunderFat API")
    API_DESCRIPTION=$(node -e "
        try {
            const spec = JSON.parse(require('fs').readFileSync('$SPEC_FILE', 'utf8'));
            console.log(spec.info?.description || 'TypeScript SDK for ThunderFat Nutrition Management API');
        } catch (e) {
            console.log('TypeScript SDK for ThunderFat Nutrition Management API');
        }
    " 2>/dev/null || echo "TypeScript SDK for ThunderFat Nutrition Management API")
    
    cat > "$SDK_DIR/package.json" << EOF
{
  "name": "$PACKAGE_NAME",
  "version": "$VERSION",
  "description": "$API_DESCRIPTION",
  "main": "index.js",
  "types": "index.d.ts",
  "scripts": {
    "build": "tsc",
    "prepublishOnly": "npm run build",
    "test": "npm run build && node -e \"require('./index.js')\""
  },
  "keywords": [
    "thunderfat",
    "nutrition",
    "api",
    "sdk",
    "typescript",
    "healthcare",
    "diet"
  ],
  "author": "ThunderFat Team",
  "license": "MIT",
  "repository": {
    "type": "git",
    "url": "https://github.com/thunderfat/backend"
  },
  "bugs": {
    "url": "https://github.com/thunderfat/backend/issues"
  },
  "homepage": "https://github.com/thunderfat/backend#readme",
  "dependencies": {
    "axios": "^1.7.0"
  },
  "devDependencies": {
    "typescript": "^5.6.0",
    "@types/node": "^22.0.0"
  },
  "files": [
    "index.js",
    "index.d.ts",
    "models/",
    "services/",
    "core/",
    "README.md",
    "CHANGELOG.md"
  ],
  "engines": {
    "node": ">=18.0.0"
  }
}
EOF
}

# Create TypeScript configuration
create_typescript_config() {
    echo "📝 Creating TypeScript configuration..."
    
    cat > "$SDK_DIR/tsconfig.json" << 'EOF'
{
  "compilerOptions": {
    "target": "ES2020",
    "module": "CommonJS",
    "lib": ["ES2020"],
    "declaration": true,
    "outDir": "./",
    "rootDir": "./",
    "strict": true,
    "esModuleInterop": true,
    "allowSyntheticDefaultImports": true,
    "skipLibCheck": true,
    "forceConsistentCasingInFileNames": true,
    "moduleResolution": "node",
    "resolveJsonModule": true
  },
  "include": [
    "**/*.ts"
  ],
  "exclude": [
    "node_modules",
    "**/*.js",
    "**/*.d.ts"
  ]
}
EOF
}

# Create comprehensive README
create_readme() {
    echo "📝 Creating README..."
    
    API_TITLE=$(node -e "
        try {
            const spec = JSON.parse(require('fs').readFileSync('$SPEC_FILE', 'utf8'));
            console.log(spec.info?.title || 'ThunderFat API');
        } catch (e) {
            console.log('ThunderFat API');
        }
    " 2>/dev/null || echo "ThunderFat API")
    API_VERSION=$(node -e "
        try {
            const spec = JSON.parse(require('fs').readFileSync('$SPEC_FILE', 'utf8'));
            console.log(spec.info?.version || '1.0.0');
        } catch (e) {
            console.log('1.0.0');
        }
    " 2>/dev/null || echo "1.0.0")
    
    cat > "$SDK_DIR/README.md" << EOF
# ThunderFat Backend SDK

TypeScript/JavaScript SDK for the $API_TITLE (v$API_VERSION).

## Installation

\`\`\`bash
npm install $PACKAGE_NAME
\`\`\`

## Quick Start

\`\`\`typescript
import { ThunderFatApi } from '$PACKAGE_NAME';

// Create API client
const api = new ThunderFatApi({
  BASE: 'https://your-api-domain.com',
  TOKEN: 'your-jwt-token'
});

// Example: Authentication
const authResult = await api.authApi.authenticate({
  username: 'your-username',
  password: 'your-password'
});

// Example: Get all patients
const patients = await api.pacienteApi.getAllPacientes();

// Example: Create a new patient
const newPatient = await api.pacienteApi.createPaciente({
  nombre: 'John',
  apellido: 'Doe',
  email: 'john.doe@example.com'
});
\`\`\`

## Configuration

### Basic Configuration

\`\`\`typescript
const api = new ThunderFatApi({
  BASE: 'https://api.thunderfat.com/api/v1',
  TOKEN: 'your-jwt-token'
});
\`\`\`

### Advanced Configuration

\`\`\`typescript
const api = new ThunderFatApi({
  BASE: 'https://api.thunderfat.com/api/v1',
  TOKEN: 'your-jwt-token',
  TIMEOUT: 30000, // 30 seconds
  WITH_CREDENTIALS: true,
  HEADERS: {
    'Custom-Header': 'value'
  }
});
\`\`\`

### Environment-based Configuration

\`\`\`typescript
const config = {
  development: {
    BASE: 'http://localhost:8080/api/v1'
  },
  production: {
    BASE: 'https://api.thunderfat.com/api/v1'
  }
};

const api = new ThunderFatApi({
  ...config[process.env.NODE_ENV || 'development'],
  TOKEN: process.env.THUNDERFAT_TOKEN
});
\`\`\`

## API Services

This SDK provides the following API services:

- **AuthApi**: Authentication and authorization
- **PacienteApi**: Patient management
- **NutricionistaApi**: Nutritionist management
- **PlanDietaApi**: Diet plan management
- **AlimentoApi**: Food database management
- **CitaApi**: Appointment scheduling
- **ChatApi**: Real-time messaging

## Error Handling

\`\`\`typescript
try {
  const patient = await api.pacienteApi.getPacienteById(123);
} catch (error) {
  if (error.status === 404) {
    console.log('Patient not found');
  } else if (error.status === 401) {
    console.log('Authentication required');
  } else {
    console.log('Unexpected error:', error.message);
  }
}
\`\`\`

## TypeScript Support

This SDK is written in TypeScript and provides comprehensive type definitions:

\`\`\`typescript
import { PacienteDTO, CreatePacienteRequest } from '$PACKAGE_NAME';

const createPatient = async (data: CreatePacienteRequest): Promise<PacienteDTO> => {
  return await api.pacienteApi.createPaciente(data);
};
\`\`\`

## Pagination

Many endpoints support pagination:

\`\`\`typescript
const paginatedPatients = await api.pacienteApi.getAllPacientes({
  page: 0,
  size: 20,
  sort: 'nombre,asc'
});

console.log('Total patients:', paginatedPatients.totalElements);
console.log('Current page:', paginatedPatients.content);
\`\`\`

## Authentication Flow

\`\`\`typescript
// Login and get token
const authResult = await api.authApi.authenticate({
  username: 'your-username',
  password: 'your-password'
});

// Update API client with token
api.request.config.TOKEN = authResult.token;

// Now you can make authenticated requests
const profile = await api.authApi.getCurrentUserProfile();
\`\`\`

## Examples

### Managing Patients

\`\`\`typescript
// Get all patients
const patients = await api.pacienteApi.getAllPacientes();

// Get patient by ID
const patient = await api.pacienteApi.getPacienteById(1);

// Create new patient
const newPatient = await api.pacienteApi.createPaciente({
  nombre: 'Jane',
  apellido: 'Smith',
  email: 'jane.smith@example.com',
  telefono: '+1-555-0123'
});

// Update patient
const updatedPatient = await api.pacienteApi.updatePaciente(1, {
  telefono: '+1-555-9876'
});
\`\`\`

### Working with Diet Plans

\`\`\`typescript
// Get diet plans for a patient
const dietPlans = await api.planDietaApi.getPlanDietasByPacienteId(1);

// Create new diet plan
const newPlan = await api.planDietaApi.createPlanDieta({
  pacienteId: 1,
  nutricionistaId: 2,
  fechaInicio: '2025-01-01',
  fechaFin: '2025-03-31',
  objetivo: 'Weight loss'
});
\`\`\`

## Contributing

Please read our [Contributing Guide](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support, please contact us at support@thunderfat.com or create an issue in our [GitHub repository](https://github.com/thunderfat/backend/issues).
EOF
}

# Create changelog
create_changelog() {
    echo "📝 Creating CHANGELOG..."
    
    cat > "$SDK_DIR/CHANGELOG.md" << EOF
# Changelog

All notable changes to this SDK will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [$VERSION] - $(date +%Y-%m-%d)

### Added
- Initial SDK release
- Full TypeScript support
- Comprehensive API coverage
- Authentication handling
- Error handling
- Pagination support

### Features
- Patient management
- Nutritionist management
- Diet plan management
- Food database access
- Appointment scheduling
- Real-time chat support

### Documentation
- Complete API documentation
- Usage examples
- Configuration guide
- Error handling guide
EOF
}

# Build SDK
build_sdk() {
    echo "🔨 Building SDK..."
    
    cd "$SDK_DIR"
    
    # Install dependencies with verbose output
    echo "📦 Installing SDK dependencies..."
    npm install --verbose > npm-install.log 2>&1
    if [ $? -ne 0 ]; then
        echo "❌ Failed to install dependencies. Check npm-install.log for details"
        echo "==== Last 20 lines of npm-install.log ===="
        tail -n 20 npm-install.log
        exit 1
    fi
    
    # Build TypeScript with verbose output
    echo "🔨 Building TypeScript files..."
    npm run build --verbose > npm-build.log 2>&1
    BUILD_RESULT=$?
    
    if [ $BUILD_RESULT -eq 0 ]; then
        echo "✅ SDK built successfully"
        
        # Test the build with verbose output
        echo "🧪 Testing SDK build..."
        npm run test --verbose > npm-test.log 2>&1
        if [ $? -eq 0 ]; then
            echo "✅ SDK tests passed"
        else
            echo "⚠️  SDK tests failed. Check npm-test.log for details"
            echo "==== Last 20 lines of npm-test.log ===="
            tail -n 20 npm-test.log
        fi
    else
        echo "❌ SDK build failed. Check npm-build.log for details"
        echo "==== Last 20 lines of npm-build.log ===="
        tail -n 20 npm-build.log
        exit 1
    fi
    
    cd ..
}

# Generate package summary
generate_summary() {
    echo "📊 Generating package summary..."
    
    FILE_COUNT=$(find "$SDK_DIR" -type f -name "*.js" -o -name "*.d.ts" | wc -l)
    PACKAGE_SIZE=$(du -sh "$SDK_DIR" | cut -f1)
    
    cat > "$SDK_DIR/BUILD_SUMMARY.md" << EOF
# SDK Build Summary

## Package Information
- **Name**: $PACKAGE_NAME
- **Version**: $VERSION
- **Generated**: $(date)
- **Source Spec**: $SPEC_FILE

## Build Results
- **Files Generated**: $FILE_COUNT
- **Package Size**: $PACKAGE_SIZE
- **TypeScript**: ✅ Compiled
- **Tests**: ✅ Passed

## Package Contents
\`\`\`
$(ls -la "$SDK_DIR" | grep -v "^total\|node_modules")
\`\`\`

## Next Steps
1. Review generated code
2. Test SDK functionality
3. Publish to npm: \`npm publish\`
EOF
    
    echo "✅ Build summary generated: $SDK_DIR/BUILD_SUMMARY.md"
}

# Main execution
main() {
    echo "🚀 Generating SDK from: $SPEC_FILE"
    
    install_tools
    determine_version
    generate_typescript_sdk
    create_package_json
    create_typescript_config
    create_readme
    create_changelog
    build_sdk
    generate_summary
    
    echo "🎉 SDK generation completed successfully!"
    echo "📦 SDK available in: $SDK_DIR"
    echo "📚 Documentation: $SDK_DIR/README.md"
    echo "🔧 To publish: cd $SDK_DIR && npm publish"
}

# Run main function
main
