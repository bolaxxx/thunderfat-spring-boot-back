# ThunderFat API Documentation

## Overview

ThunderFat is a nutrition and dietary management system. This API provides endpoints for managing predetermined dishes, diet plans, nutritionist services, and patient records. This document provides detailed information about the endpoints, authentication, and usage patterns.

## API Versioning

The API supports both legacy and modern endpoints:

- Legacy endpoints: `/platopredeterminado/*`
- Modern endpoints: `/api/v1/dishes/*` and `/api/v1/nutritionists/{id}/dishes/*`

We recommend using the modern endpoints for all new development as they follow REST best practices and return consistent response formats.

## Authentication

The API uses JSON Web Tokens (JWT) for authentication. To access protected endpoints, you need to:

1. Obtain a JWT token by authenticating with `/api/auth/login`
2. Include the token in all subsequent requests in the `Authorization` header:

```http
Authorization: Bearer <your_jwt_token>
```

## Common Response Format

All modern API endpoints return responses in a consistent format:

```json
{
  "data": {
    // Response data specific to the endpoint
  },
  "message": "Success or error message",
  "status": "SUCCESS" | "ERROR",
  "timestamp": "2023-06-15T10:30:45Z"
}
```

## PlatoPredeterminado (Predetermined Dish) API

### Modern Endpoints

#### Get Dish by ID

```http
GET /api/v1/dishes/{id}
```

**Parameters:**

- `id` - The unique identifier of the dish (Path Variable)

**Response:**

- `200 OK` - Returns the dish details
- `404 Not Found` - Dish not found
- `500 Internal Server Error` - Error retrieving dish

#### List Dishes by Nutritionist

```http
GET /api/v1/nutritionists/{nutricionistaId}/dishes
```

**Parameters:**

- `nutricionistaId` - The ID of the nutritionist (Path Variable)
- `page` - Page number (Query Parameter, default: 0)
- `size` - Page size (Query Parameter, default: 10)

**Response:**

- `200 OK` - Returns a list of dishes for the specified nutritionist
- `500 Internal Server Error` - Error retrieving dishes

#### Create New Dish

```http
POST /api/v1/nutritionists/{nutricionistaId}/dishes
```

**Parameters:**

- `nutricionistaId` - The ID of the nutritionist (Path Variable)
- Request body: PlatoPredeterminadoDTO object

**Required Fields:**

- `nombre` - Name of the dish
- `kcaltotales` - Total calories
- Additional nutritional fields as needed

**Response:**

- `201 Created` - Dish created successfully
- `400 Bad Request` - Invalid input data
- `500 Internal Server Error` - Error creating dish

#### Delete Dish

```http
DELETE /api/v1/dishes/{id}
```

**Parameters:**

- `id` - The unique identifier of the dish (Path Variable)

**Response:**

- `204 No Content` - Dish deleted successfully
- `404 Not Found` - Dish not found
- `500 Internal Server Error` - Error deleting dish

### Legacy Endpoints

The following legacy endpoints are maintained for backward compatibility:

- `GET /platopredeterminado/{id}` - Get dishes by nutritionist
- `GET /platopredeterminado/detalle/{id}` - Get dish details
- `POST /platopredeterminado/save/{id}` - Create new dish
- `DELETE /platopredeterminado/delete/{id}` - Delete dish
- `PUT /platopredeterminado/actualizar/{id}` - Update dish

## Caching Strategy

The API implements a sophisticated caching strategy to optimize performance:

1. In production, Redis is used as the primary caching provider
2. In development, Caffeine is used as a fallback in-memory caching provider
3. Conditional caching is implemented based on:
   - Input parameter validation (e.g., positive IDs)
   - Result set characteristics (e.g., non-empty results)
   - Result set size (e.g., limiting cache entries for large result sets)

Cache TTL (Time to Live) varies by resource type:

- Short-lived resources: 5 minutes
- Standard resources: 30 minutes
- Reference data: 1 hour

## Rate Limiting

The API implements rate limiting to prevent abuse:

- 100 requests per minute for authenticated users
- 10 requests per minute for unauthenticated users

Exceeding these limits will result in HTTP 429 (Too Many Requests) responses.

## Error Handling

The API uses standard HTTP status codes and includes detailed error messages in the response body:

```json
{
  "data": null,
  "message": "Detailed error message",
  "status": "ERROR",
  "timestamp": "2023-06-15T10:30:45Z"
}
```

Common error codes:

- `400 Bad Request` - Invalid input data
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `429 Too Many Requests` - Rate limit exceeded
- `500 Internal Server Error` - Server-side error

## API Explorer

The API documentation can be explored interactively using the Swagger UI at:

```http
http://localhost:8080/swagger-ui.html
```

The OpenAPI specification is available at:

```http
http://localhost:8080/api-docs
```

## Client Libraries

Official client libraries are available for:

- JavaScript/TypeScript
- Java
- Python

## Support

For API support, contact:

- Email: api-support@thunderfat.com
- Developer Portal: [https://developers.thunderfat.com](https://developers.thunderfat.com)
