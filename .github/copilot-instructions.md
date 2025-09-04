# AI Coding Instructions for ThunderFat Spring Boot Backend

## Project Overview
This is a **nutrition management system** built with **Spring Boot 3.5.4** and **Java 21**, designed for nutritionists to manage patients, diet plans, appointments, and meal tracking. The system follows modern Spring Boot 2025 best practices with a strong emphasis on healthcare data security, performance optimization, and scalable architecture.

## Architecture & Key Patterns

### Core Domain Structure
- **Nutrition Management**: Alimentos (foods), diet plans, meal tracking, and nutritional calculations
- **Patient Management**: Patient profiles, medical history, body measurements, and progress tracking  
- **Appointment System**: Scheduling, conflict detection, and calendar integration
- **Real-time Communication**: WebSocket-based chat system between nutritionists and patients
- **Security**: Multi-layered JWT + OAuth2 authentication with role-based access control

### Modern Spring Boot 2025 Patterns
1. **DTO-First Architecture**: All external APIs use DTOs, never expose entities directly
2. **MapStruct Integration**: Comprehensive entity↔DTO mapping with `@Mapper(componentModel = "spring")`
3. **Multi-level Caching**: Repository, service, and controller-level caching with named cache regions
4. **Constructor Injection**: `@RequiredArgsConstructor` with `final` fields throughout
5. **Security by Default**: Method-level `@PreAuthorize` on sensitive operations

### Package Organization
```
com.thunderfat.springboot.backend/
├── auth/                    # JWT, OAuth2, security filters
├── config/                  # Caching, WebSocket, OpenAPI configuration  
├── controllers/             # REST endpoints with OpenAPI docs
├── exception/               # Global exception handling
├── model/
│   ├── dao/                # Repository interfaces (extend BaseRepository)
│   ├── dto/                # Data transfer objects with validation
│   │   └── mapper/         # MapStruct mappers 
│   ├── entity/             # JPA entities
│   └── service/            # Business logic implementations
└── validation/             # Custom validation groups and validators
```

## Development Workflows

### Building & Testing
```bash
# Primary build command (use this for compilation checks)
./mvnw clean compile

# Run tests (when implementing new features)
./mvnw test

# Full build with packaging
./mvnw clean package
```

### Database Setup
- **Local Development**: MySQL on `localhost:3306/thunderfatboot`
- **Schema Management**: Hibernate `create-drop` mode (auto-creates tables)
- **Credentials**: `root/sasa` (development only)

### Running the Application
```bash
# Standard startup
./mvnw spring-boot:run

# Development with hot reload
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
```

## Critical Development Guidelines

### Authentication & Security
- **NEVER** bypass `@PreAuthorize` annotations on service methods
- **JWT Configuration**: RSA key pair in `JwtConfig.java` (development keys only)
- **Security Context**: Main config is `SpringSecurityConfig.java` (note: `@Configuration` is temporarily disabled)
- **CORS**: Configured for `localhost:4200` (Angular) and `localhost:8100` (Ionic) 
- **Public Endpoints**: `/api/auth/**`, `/actuator/**`, `/swagger-ui/**` don't require authentication

### Service Layer Patterns
```java
@Service
@Transactional
@RequiredArgsConstructor  
@Slf4j
public class ExampleServiceJPA implements IExampleService {
    
    private final ExampleRepository repository;
    private final ExampleMapper mapper;
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "example-cache", key = "#id")
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('ADMIN')")
    public ExampleDTO findById(Integer id) {
        Example entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Example not found"));
        return mapper.toDto(entity);
    }
}
```

### DTO & Validation Patterns
```java
@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExampleDTO {
    private Integer id;
    
    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(min = 2, max = 100)
    private String name;
    
    @PositiveOrZero(message = "Value cannot be negative")
    private Double value;
}
```

### MapStruct Mapping
```java
@Mapper(componentModel = "spring", uses = {RelatedMapper.class})
public interface ExampleMapper {
    
    @Mapping(source = "relatedEntity.id", target = "relatedId")
    @Mapping(target = "calculatedField", expression = "java(calculateField(entity))")
    ExampleDTO toDto(Example entity);
    
    @Mapping(target = "id", ignore = true)
    Example toEntity(ExampleDTO dto);
}
```

### Caching Strategy
- **Cache Names**: Defined as constants in `CacheConfig.java`
- **Cache Eviction**: Use `@CacheEvict(allEntries = true)` for write operations
- **Cache Keys**: Construct meaningful keys like `"patient_" + #patientId`
- **Multi-cache Operations**: Use `@Caching` for complex eviction patterns

## Project-Specific Domain Knowledge

### Nutrition Data Management
- **Alimentos**: Core food database with detailed nutritional information (proteins, vitamins, minerals, amino acids)
- **PlanDieta**: Diet plans with start/end dates, associated with specific patients and nutritionists
- **DiaDieta**: Individual days within diet plans containing meal schedules
- **Comida**: Meals with specific foods and quantities

### Patient Data Hierarchy
- **Paciente** → **PlanDieta** → **DiaDieta** → **Comida** → **Alimentos**
- **MedicionSegmental**: Body composition measurements (arms, legs, torso)
- **AntecedentesClinicos**: Medical history and clinical background

### Business Rules to Respect
1. **Data Ownership**: Nutritionists can only access their assigned patients
2. **Appointment Conflicts**: System validates scheduling conflicts automatically
3. **Diet Plan Validity**: Plans have active date ranges and completion tracking
4. **Nutritional Calculations**: Automatic computation of daily nutritional totals

### API Response Patterns
- **Success**: Use `ManualApiResponseDTO.success()` wrapper
- **Errors**: Use `ManualApiResponseDTO.error()` with descriptive messages
- **Pagination**: All list endpoints support `Pageable` parameters
- **OpenAPI**: All controllers have comprehensive `@Operation` documentation

## Testing Approach
- **Integration Tests**: Focus on service layer with `@SpringBootTest`
- **Security Testing**: Verify `@PreAuthorize` enforcement
- **Repository Testing**: Use `@DataJpaTest` for database operations
- **Mock External Dependencies**: Cache, security context, and external APIs

## Performance Considerations
- **Connection Pooling**: HikariCP optimized for 20 max connections
- **JPA Optimization**: Batch operations, `@EntityGraph` for efficient loading
- **Caching**: Multi-tier strategy with TTL-based eviction
- **Database**: MySQL with UTC timezone, batch processing enabled

When implementing new features, always follow these established patterns for consistency and maintainability. The codebase prioritizes healthcare data security, performance, and clear separation of concerns.
