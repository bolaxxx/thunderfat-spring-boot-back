# ThunderFat Spring Boot Backend

ThunderFat is a nutrition and dietary management system built with Spring Boot. This API provides endpoints for managing predetermined dishes, diet plans, nutritionist services, and patient records.

## API Documentation

We provide comprehensive API documentation through multiple formats:

1. **OpenAPI/Swagger UI**: Access interactive API documentation at `/swagger-ui.html` when the application is running.
2. **OpenAPI JSON/YAML**: The raw OpenAPI specification is available at `/api-docs`.
3. **API Guide**: See [API-DOCUMENTATION.md](./API-DOCUMENTATION.md) for a comprehensive guide to using the API.

## Key Features

- **Modern REST API Design**: Following Spring Boot 2025 best practices
- **Entity Relationships**: Proper relationships between entities (e.g., Nutricionista to PlatoPredeterminado)
- **Advanced Caching**: Multi-tier caching strategy with Redis and Caffeine
- **Comprehensive Testing**: Unit and integration tests for all major components
- **API Versioning**: Support for both legacy and modern API endpoints
- **Security**: JWT-based authentication and role-based access control
- **Validation**: Input validation using Jakarta Validation annotations
- **Documentation**: OpenAPI 3.0 documentation with Swagger UI

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- MySQL 8.0 or higher
- Redis (optional, for production caching)

### Running the Application

1. Clone the repository
2. Configure database connection in `application.properties`
3. Run the application:

```bash
./mvnw spring-boot:run
```

Then:

1. Access the API at `http://localhost:8080`
2. Access the API documentation at `http://localhost:8080/swagger-ui.html`

### Running Tests

```bash
./mvnw test
```

### Building the Application

```bash
./mvnw clean package
```

## Architecture

The application follows a layered architecture:

- **Controllers**: REST controllers for API endpoints
- **Services**: Business logic implementation
- **Repositories**: Data access layer
- **DTOs**: Data transfer objects for API requests/responses
- **Entities**: JPA entities for database persistence
- **Mappers**: Object mappers for converting between entities and DTOs
- **Validation**: Input validation rules
- **Configuration**: Application configuration
- **Exception Handling**: Global exception handlers

## Caching Strategy

The application implements a sophisticated caching strategy:

- **Production**: Redis for distributed caching
- **Development**: Caffeine for in-memory caching
- **Conditional Caching**: Only cache valid results based on conditions
- **Cache Eviction**: Proper cache eviction on updates/deletes

## API Design Principles

The API follows modern REST API design principles:

- **Resource-based URLs**: `/api/v1/nutritionists/{id}/dishes`
- **HTTP Methods**: GET, POST, PUT, DELETE for CRUD operations
- **Status Codes**: Appropriate HTTP status codes
- **Response Format**: Consistent response format
- **Validation**: Input validation with meaningful error messages
- **Pagination**: Support for pagination of large result sets
- **API Versioning**: Version prefix in URL (/api/v1/...)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
