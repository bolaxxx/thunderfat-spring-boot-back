# ThunderFat Backend SDK

TypeScript/JavaScript SDK for the ThunderFat Nutrition Management API (v1.0.0).

## Installation

```bash
npm install @thunderfat/backend-sdk
```

## Quick Start

```typescript
import { ThunderFatApi } from '@thunderfat/backend-sdk';

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
```

## Configuration

### Basic Configuration

```typescript
const api = new ThunderFatApi({
  BASE: 'https://api.thunderfat.com/api/v1',
  TOKEN: 'your-jwt-token'
});
```

### Advanced Configuration

```typescript
const api = new ThunderFatApi({
  BASE: 'https://api.thunderfat.com/api/v1',
  TOKEN: 'your-jwt-token',
  TIMEOUT: 30000, // 30 seconds
  WITH_CREDENTIALS: true,
  HEADERS: {
    'Custom-Header': 'value'
  }
});
```

### Environment-based Configuration

```typescript
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
```

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

```typescript
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
```

## TypeScript Support

This SDK is written in TypeScript and provides comprehensive type definitions:

```typescript
import { PacienteDTO, CreatePacienteRequest } from '@thunderfat/backend-sdk';

const createPatient = async (data: CreatePacienteRequest): Promise<PacienteDTO> => {
  return await api.pacienteApi.createPaciente(data);
};
```

## Pagination

Many endpoints support pagination:

```typescript
const paginatedPatients = await api.pacienteApi.getAllPacientes({
  page: 0,
  size: 20,
  sort: 'nombre,asc'
});

console.log('Total patients:', paginatedPatients.totalElements);
console.log('Current page:', paginatedPatients.content);
```

## Authentication Flow

```typescript
// Login and get token
const authResult = await api.authApi.authenticate({
  username: 'your-username',
  password: 'your-password'
});

// Update API client with token
api.request.config.TOKEN = authResult.token;

// Now you can make authenticated requests
const profile = await api.authApi.getCurrentUserProfile();
```

## Examples

### Managing Patients

```typescript
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
```

### Working with Diet Plans

```typescript
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
```

## Contributing

Please read our [Contributing Guide](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support, please contact us at support@thunderfat.com or create an issue in our [GitHub repository](https://github.com/thunderfat/backend/issues).
