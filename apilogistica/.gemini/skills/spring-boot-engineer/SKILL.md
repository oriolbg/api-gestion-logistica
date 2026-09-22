---
name: spring-boot-engineer
description: Generates Spring Boot 3.x configurations, creates REST controllers, implements Spring Security 6 authentication flows, sets up Spring Data JPA repositories, and configures reactive WebFlux endpoints. Use when building Spring Boot 3.x applications, microservices, or reactive Java applications; invoke for Spring Data JPA, Spring Security 6, WebFlux, Spring Cloud integration, Java REST API design, or Microservices Java architecture.
license: MIT
metadata:
  author: https://github.com/Jeffallan
  version: "1.1.0"
  domain: backend
  triggers: Spring Boot, Spring Framework, Spring Cloud, Spring Security, Spring Data JPA, Spring WebFlux, Microservices Java, Java REST API, Reactive Java
  role: specialist
  scope: implementation
  output-format: code
  related-skills: java-architect, database-optimizer, microservices-architect, devops-engineer
---

# Spring Boot Engineer

## Archetype Selection (Red Link Standard)

Before starting, choose the appropriate archetype based on project complexity:

- **ms-layer (Layered Architecture):** Recommended for simple services with stable requirements and direct DB interaction. Uses standard `Controller -> Service -> Repository` flow.
- **ms-hexa (Hexagonal Architecture):** **PREFERRED** for complex, evolving services with multiple external integrations. Isolates core business logic from infrastructure using Ports and Adapters.

## Core Workflow

1. **Analyze requirements** — Identify service boundaries, APIs, data models, security needs. Select archetype (`ms-hexa` or `ms-layer`).
2. **Design architecture** — Plan microservices, data access, cloud integration, security; confirm design before coding.
3. **Implement** — Create services with constructor injection and the chosen architecture. Use **Java 21** features (Records, Sealed Classes).

## Quick Start — Hexagonal Reference Structure

Following the `ms-hexa` archetype standard:

### Domain Model (Pure POJO/Record)
```java
public record Product(Long id, String name, BigDecimal price) {}
```

### Input Port (Application Layer)
```java
public interface CreateProductUseCase {
    Product execute(ProductRequest request);
}
```

### Use Case Implementation (Application Layer)
```java
@Service // In Hexagonal, often registered via @Configuration to avoid @Service in application layer
public class CreateProductService implements CreateProductUseCase {
    private final ProductPersistencePort persistencePort;

    public CreateProductService(ProductPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    @Override
    @Transactional
    public Product execute(ProductRequest request) {
        // Business logic here
        return persistencePort.save(new Product(null, request.name(), request.price()));
    }
}
```

### REST Controller (Input Adapter)
```java
@RestController
@RequestMapping("/api/v1/products")
@Validated
public class ProductController {
    private final CreateProductUseCase useCase;

    public ProductController(CreateProductUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        Product domain = useCase.execute(request);
        return ProductResponse.from(domain);
    }
}
```

### Global Exception Handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Map<String, String> handleBusiness(BusinessException ex) {
        return Map.of("error", ex.getMessage(), "code", ex.getCode());
    }
}
```

## Constraints

### MUST DO

| Rule | Correct Pattern |
|------|----------------|
| Constructor injection | `public MyService(Dep dep) { this.dep = dep; }` |
| Java 21 Records | `public record MyDto(String name) {}` for all DTOs and immutable models |
| Architecture Alignment | Follow either `ms-hexa` or `ms-layer` folder structure strictly |
| Validate API input | `@Valid @RequestBody MyRequest req` on every mutating endpoint |
| Externalize secrets | Use environment variables or Spring Cloud Config — never `application.properties` |


### MUST NOT DO
- Use field injection (`@Autowired` on fields)
- Skip input validation on API endpoints
- Use `@Component` when `@Service`/`@Repository`/`@Controller` applies
- Mix blocking and reactive code (e.g., calling `.block()` inside a WebFlux chain)
- Store secrets or credentials in `application.properties`/`application.yml`
- Hardcode URLs, credentials, or environment-specific values
- Use deprecated Spring Boot 2.x patterns (e.g., `WebSecurityConfigurerAdapter`)
