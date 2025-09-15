# ThunderFat Backend Deployment Pipeline

This repository includes a comprehensive deployment pipeline for the ThunderFat Spring Boot backend with API contract management, SDK generation, and semantic versioning.

## 🚀 Quick Start

### Prerequisites

- Java 21+
- Maven 3.8+
- Node.js 18+ (for API tools)
- Git

### Initial Setup

```bash
# Install Node.js dependencies for API tools
npm install

# Make scripts executable (Unix/Linux/macOS)
chmod +x scripts/*.sh

# Verify setup
npm run test:scripts
```

## 📋 Pipeline Overview

The deployment pipeline follows these stages:

1. **Export API Spec** - Boot app, export `/v3/api-docs` → `api/openapi.json`
2. **Validate & Lint** - Use Spectral to lint spec and detect breaking changes
3. **Generate SDK** - Create TypeScript SDK with openapi-typescript-codegen
4. **Release Management** - Semantic versioning with automated changelog
5. **Deployment** - Deploy to target environment with health checks

## 🛠️ Available Scripts

### Core Pipeline Scripts

```bash
# Export OpenAPI specification from running application
npm run export-api
# OR
./scripts/export-api-spec.sh [port] [output-file] [timeout]

# Validate and lint API specification  
npm run validate-api
# OR
./scripts/validate-api-spec.sh [spec-file] [previous-spec] [fail-on-breaking]

# Generate TypeScript SDK
npm run generate-sdk
# OR  
./scripts/generate-sdk.sh [spec-file] [sdk-dir] [package-name] [version]

# Full development workflow
npm run dev:full
```

### Release Management

```bash
# Create releases with semantic versioning
npm run release:patch    # Bug fixes, docs updates
npm run release:minor    # New features (backward compatible)
npm run release:major    # Breaking changes

# Dry run (test without making changes)
npm run release:dry-run

# Manual release
./scripts/release.sh [major|minor|patch] [dry-run]
```

### Deployment

```bash
# Deploy to different environments
./scripts/deploy.sh [environment] [build-profile] [skip-tests]

# Examples:
./scripts/deploy.sh development dev false
./scripts/deploy.sh staging test false  
./scripts/deploy.sh production prod true
```

## 🏗️ GitHub Actions Workflow

The repository includes a comprehensive GitHub Actions workflow (`.github/workflows/api-contract-pipeline.yml`) that:

### On Push/PR:
1. Starts MySQL service
2. Boots Spring Boot application
3. Exports OpenAPI specification
4. Validates with Spectral linting
5. Detects breaking changes vs. previous version
6. Generates TypeScript SDK
7. Packages SDK for npm
8. Creates GitHub releases
9. Updates changelog

### Trigger Conditions:
- **Push**: `main`, `master`, `develop` branches
- **Pull Request**: `main`, `master` branches

## 📦 SDK Generation

The pipeline automatically generates a TypeScript SDK with:

- **Package Name**: `@thunderfat/backend-sdk`
- **Features**: 
  - Full TypeScript support
  - Axios-based HTTP client
  - Automatic request/response typing
  - Built-in authentication handling
  - Comprehensive error handling

### SDK Usage Example:

```typescript
import { ThunderFatApi } from '@thunderfat/backend-sdk';

const api = new ThunderFatApi({
  BASE: 'https://api.thunderfat.com',
  TOKEN: 'your-jwt-token'
});

// Get all patients
const patients = await api.pacienteApi.getAllPacientes();

// Create new patient
const newPatient = await api.pacienteApi.createPaciente({
  nombre: 'John',
  apellido: 'Doe', 
  email: 'john.doe@example.com'
});
```

## 🔍 API Validation

### Spectral Linting Rules

The pipeline uses Spectral with custom rules for healthcare APIs:

- **Operation Rules**: Require operation IDs, descriptions, tags
- **Parameter Rules**: Validate path parameters and references
- **Schema Rules**: Enforce proper response schemas
- **Security Rules**: Ensure protected endpoints have security
- **Custom Rules**: ThunderFat-specific naming conventions

### Breaking Change Detection

The pipeline automatically detects breaking changes by comparing:
- Removed endpoints or parameters
- Changed parameter types
- Modified response schemas
- Security requirement changes

## 📊 Semantic Versioning Strategy

- **PATCH** (`x.x.X`): Documentation updates, bug fixes, non-breaking changes
- **MINOR** (`x.X.x`): New features, additive fields/endpoints
- **MAJOR** (`X.x.x`): Breaking changes, require `/api/v2` introduction

## 📄 Generated Artifacts

### Every Release:
- `api/openapi.json` - OpenAPI 3.1 specification
- `sdk/` - Complete TypeScript SDK package
- `CHANGELOG_API.md` - Human-readable change notes
- GitHub release with downloadable assets
- npm package: `@thunderfat/backend-sdk`

### Validation Reports:
- `reports/spectral-junit.xml` - Linting results
- `reports/api-diff.json` - Breaking change analysis
- `reports/validation-summary.md` - Summary report

## 🌍 Environment Configuration

### Development
- **URL**: `http://localhost:8080`
- **Profile**: `dev`
- **Database**: Local MySQL
- **Features**: Hot reload, debug logging

### Staging
- **URL**: `https://staging-api.thunderfat.com`
- **Profile**: `test`
- **Database**: Staging MySQL
- **Features**: Production-like testing

### Production
- **URL**: `https://api.thunderfat.com`
- **Profile**: `prod`
- **Database**: Production MySQL
- **Features**: Optimized performance, monitoring

## 📚 API Documentation

### Automatic Documentation:
- **OpenAPI Spec**: `/v3/api-docs`
- **Swagger UI**: `/swagger-ui/index.html`
- **ReDoc**: `/redoc` (if configured)

### Endpoints:
- **Health Check**: `/actuator/health`
- **Application Info**: `/actuator/info`
- **Metrics**: `/actuator/metrics`

## 🔧 Configuration Files

### Core Files:
- `.spectral.yml` - API linting rules
- `package.json` - Node.js tooling
- `.github/workflows/api-contract-pipeline.yml` - CI/CD pipeline

### Script Directory:
- `scripts/export-api-spec.sh` - API export utility
- `scripts/validate-api-spec.sh` - Validation and linting
- `scripts/generate-sdk.sh` - SDK generation
- `scripts/release.sh` - Release management
- `scripts/deploy.sh` - Deployment automation

## 🚨 Troubleshooting

### Common Issues:

#### Application Won't Start
```bash
# Check Java version
java -version  # Should be 21+

# Check port availability
netstat -tulpn | grep 8080

# Check logs
tail -f logs/app.log
```

#### API Export Fails
```bash
# Check application health
curl http://localhost:8080/actuator/health

# Manual export
curl http://localhost:8080/v3/api-docs > api/openapi.json
```

#### SDK Generation Fails
```bash
# Check Node.js version
node --version  # Should be 18+

# Install tools manually
npm install -g openapi-typescript-codegen
```

#### Breaking Changes Detected
1. Review the changes in `reports/api-diff.json`
2. Consider creating `/api/v2` endpoints
3. Deprecate old endpoints before removing
4. Update migration documentation

## 🤝 Contributing

### Development Workflow:
1. Create feature branch
2. Make changes
3. Test locally: `npm run dev:full`
4. Create pull request
5. Pipeline validates changes automatically
6. Merge triggers release process

### Release Process:
1. Determine change type (patch/minor/major)
2. Run: `npm run release:[type]`
3. Pipeline handles the rest automatically

## 📞 Support

- **Issues**: Create GitHub issue
- **Documentation**: See `/docs` directory
- **API Testing**: Use Swagger UI at `/swagger-ui/`

## 🔗 Links

- **GitHub Repository**: [thunderfat/backend](https://github.com/thunderfat/backend)
- **npm Package**: [@thunderfat/backend-sdk](https://www.npmjs.com/package/@thunderfat/backend-sdk)
- **API Documentation**: Available at `/swagger-ui/` when running
- **Health Check**: `/actuator/health`
