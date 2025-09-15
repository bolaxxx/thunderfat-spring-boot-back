# ThunderFat API Changelog

All notable changes to the ThunderFat API will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Enhanced error handling with correlation IDs
- Standardized pagination across all endpoints
- Security headers configuration (HSTS, X-Content-Type-Options, etc.)
- Contract-first development pipeline scripts
- OpenAPI 3.1 specification with comprehensive documentation

### Changed
- Updated API base path to `/api/v1` for versioning
- Improved OpenAPI documentation with operation IDs
- Enhanced CORS configuration with environment-specific origins
- Migrated to contract-compliant error responses

### Security
- Added JWT Bearer authentication with proper security schemes
- Implemented security headers for production deployment
- Enhanced validation with detailed error messages

## [1.0.0] - 2025-01-15

### Added
- Initial API implementation
- Patient management endpoints
- Nutritionist management
- Diet plan creation and management
- Medical measurements tracking
- Real-time chat functionality
- Spanish billing system integration
- Basic authentication and authorization

### Security
- JWT-based authentication
- Role-based access control
- Input validation and sanitization

---

## Development Guidelines

### API Versioning Strategy

- **MAJOR version** when you make incompatible API changes
- **MINOR version** when you add functionality in a backwards compatible manner  
- **PATCH version** when you make backwards compatible bug fixes

### Breaking Changes

When introducing breaking changes:

1. Increment MAJOR version
2. Consider introducing `/api/v2` endpoints
3. Maintain `/api/v1` compatibility for at least 6 months
4. Add deprecation warnings to v1 endpoints
5. Update client SDKs and documentation

### Non-Breaking Changes

These changes can be made without version bumps:

- Adding new optional fields to requests
- Adding new fields to responses
- Adding new endpoints
- Adding new optional query parameters
- Making required fields optional
- Adding new enum values (with proper defaults)

### Breaking Changes Examples

These require a new major version:

- Removing fields from responses
- Making optional fields required
- Changing field types
- Renaming fields or endpoints
- Removing endpoints
- Changing authentication methods
- Changing error response formats

### Release Process

1. Update this changelog
2. Run contract validation: `./export-api-contract.sh --release v1.x.x`
3. Generate and test TypeScript SDK
4. Update API documentation
5. Create git tag: `git tag v1.x.x`
6. Deploy to staging for testing
7. Deploy to production
8. Notify API consumers of changes

### SDK Versioning

TypeScript SDK versions follow API versions:

- API v1.0.0 → SDK @thunderfat/api-client@1.0.0
- API v1.1.0 → SDK @thunderfat/api-client@1.1.0  
- API v2.0.0 → SDK @thunderfat/api-client@2.0.0

### Deprecation Policy

When deprecating API features:

1. Add `@deprecated` annotation to OpenAPI spec
2. Include `Deprecation` HTTP header with removal date
3. Update this changelog with deprecation notice
4. Notify API consumers via email/documentation
5. Maintain deprecated features for minimum 6 months
6. Remove in next major version

### Testing Requirements

Before any release:

- [ ] All unit tests pass
- [ ] Integration tests pass  
- [ ] Contract tests validate API spec
- [ ] Performance tests meet SLA
- [ ] Security scan shows no critical issues
- [ ] API documentation is updated
- [ ] SDK generation succeeds
- [ ] Backward compatibility verified (for minor/patch releases)
