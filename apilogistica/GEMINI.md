# Directrices Técnicas y Contexto de Proyecto: API Gestión Logística (apilogistica)

> **Documento de Contexto y Estándares de Ingeniería**  
> Este documento sirve como punto de entrada y especificación arquitectónica tanto para desarrolladores como para asistentes de IA (Gemini / Antigravity). Define el stack tecnológico, patrones de diseño, convenciones de código y directrices de runtime orientadas a microservicios y aplicaciones Java modernas (versiones 17, 21 y 25).

---

## 1. Visión General del Proyecto

- **Nombre del Proyecto:** `apilogistica` (Sistema ERP - Gestión Logística)
- **Dominio:** Gestión de clientes, productos, pedidos, inventario, unidades de medida y administración de accesos con seguridad basada en roles.
- **Tipo de Aplicación:** API RESTful / Microservicio empresarial mediano (con extensibilidad y patrones aplicables a módulos de escritorio clientes vía JavaFX/Swing o clientes HTTP).
- **Filosofía de Diseño:** Clean Architecture pragmática, separación estricta de responsabilidades (por capas), inmutabilidad donde sea viable, contratos de API estandarizados y alta cohesión con bajo acoplamiento.

---

## 2. Stack Tecnológico y Runtime

### 2.1 Entorno de Ejecución (Java Runtime)
- **Versión Base del Proyecto:** **Java 25** (OpenJDK / GraalVM, configurado vía Gradle Toolchain `JavaLanguageVersion.of(25)`).
- **Compatibilidad del Blueprint:** Java 17 LTS / Java 21 LTS / Java 25.
- **Gestor de Construcción:** Gradle (con Gradle Wrapper `gradlew`, Groovy DSL).

### 2.2 Framework y Ecosistema
- **Core:** Spring Boot `4.0.8` (soporte completo para Jakarta EE 10/11 y Java 21+).
- **Persistencia:** Spring Data JPA + Hibernate con conector `com.mysql:mysql-connector-j`.
- **Seguridad:** Spring Security 6.x + JWT (`io.jsonwebtoken:jjwt` v0.13.0) con arquitectura Stateless.
- **Validación:** Jakarta Bean Validation (`spring-boot-starter-validation`).
- **Documentación OpenAPI:** SpringDoc OpenAPI WebMvc UI `3.1.0` (Swagger UI v3 integrado en `/swagger-ui.html`).
- **Productividad:** Project Lombok (`1.18.42`).
- **Testing:** JUnit 5 (JUnit Jupiter Platform), Mockito, Spring Boot Test Starter.

---

## 3. Matriz de Buenas Prácticas según Versión de Java (17, 21, 25)

El código debe aprovechar activamente las características de la versión de Java activa:

| Feature / Estándar | Java 17 LTS | Java 21 LTS | Java 25 |
| :--- | :--- | :--- | :--- |
| **Modelos de Datos / DTOs** | Usar `record` para DTOs inmutables de consulta / lectura. | Usar `record` + Record Patterns en deconstrucción. | Usar `record` con validaciones compactas y constructores flexibles. |
| **Concurrencia / I/O** | Platform Threads / Thread Pools tradicionales (`Executors`). | **Virtual Threads (Project Loom)**: Habilitar `spring.threads.virtual.enabled: true` para operaciones I/O bloqueantes. | Virtual Threads maduros + mejoras en pinning de carriers y sincronización. |
| **Control de Flujo** | Pattern Matching para `instanceof`, Switch Expressions. | Pattern Matching para `switch` exhaustivo con tipos y guards (`when`). | Pattern Matching avanzado y primitivos en patrones. |
| **Dominio y Jerarquías** | `sealed class` y `sealed interface` para restringir subtipos en el dominio (ej. tipos de pago, estados). | Jerarquías selladas exhaustivas combinadas con switch sin clause `default`. | Tipado enriquecido y mejoras de JVM. |
| **Cadenas y Plantillas** | Text Blocks (`"""`) para SQL nativo, JSON de prueba o plantillas de correo. | Text Blocks multilínea limpios y formateo seguro. | Nuevos procesadores de cadenas / String Templates optimizados. |

> [!IMPORTANT]
> **Virtual Threads en Microservicios (Java 21/25):**
> Al usar Spring Boot en Java 21 o 25 con tareas I/O intensivas (base de datos MySQL, llamadas REST a otros microservicios), evitar el uso de bloques `synchronized` en código nuevo que puedan provocar *thread pinning*; preferir `ReentrantLock` o estructuras concurrentes de `java.util.concurrent`.

---

## 4. Arquitectura de Paquetes y Responsabilidades

El proyecto sigue una estructura por capas estrictamente tipada:

```
erp.gestion.apilogistica/
├── config/             # Configuraciones de infraestructura (Security, OpenAPI, Data Initializers)
├── controller/         # Adaptadores primarios REST (@RestController, @RequestMapping)
├── dto/                # Data Transfer Objects (Request/Response) y contratos de datos
├── entity/             # Entidades JPA (@Entity, @Table, @Id)
├── exception/          # Excepciones de negocio y Handler Global (@ControllerAdvice)
├── mapper/             # Mapeadores Entity <-> DTO (GenericMapper y componentes dedicados)
├── repository/         # Interfaces Spring Data JPA (@Repository)
├── security/           # Filtros JWT (JwtAuthenticationFilter), servicios de tokens y UserDetails
├── service/            # Interfaces de lógica de negocio (CrudService, PageableService, etc.)
│   └── impl/           # Implementaciones (@Service, @Transactional)
├── validator/          # Validaciones de reglas de negocio específicas y complejas
└── util/               # Clases y métodos utilitarios transversales
```

### Reglas de Dependencia entre Capas:
1. **Controller:** Solo interactúa con interfaces de `Service` y tipos `DTO`. Nunca debe interactuar directamente con entidades `@Entity` ni con `Repository`.
2. **Service:** Orquesta la lógica del negocio, transaccionalidad (`@Transactional`), validaciones de dominio (`Validator`) y conversiones vía `Mapper`.
3. **Repository:** Exclusivo para operaciones de acceso a datos JPA y consultas derivadas / `@Query`.
4. **DTO vs Entity:** Las entidades JPA jamás se retornan directamente al cliente HTTP; deben convertirse a `DTO` para evitar fugas de información, acoplamiento y problemas de serialización circular (`LazyInitializationException`).

---

## 5. Estándares de Diseño y Buenas Prácticas

### 5.1 Respuestas HTTP y Formato Unificado (`WrapperResponse`)
Todos los endpoints REST deben responder encapsulando la carga útil dentro de `WrapperResponse<T>` para mantener consistencia:
```java
// Ejemplo estándar en Controller
@GetMapping("/{id}")
public ResponseEntity<WrapperResponse<ClienteDTO>> findById(@PathVariable Long id) {
    ClienteDTO dto = service.findById(id);
    return new WrapperResponse<>(dto, true, "success").createResponse(HttpStatus.OK);
}
```

### 5.2 Manejo Global de Excepciones
- Se centraliza en `ErrorHandlerConfig` mediante `@ControllerAdvice`.
- No capturar excepciones genéricas con bloques `try-catch` vacíos en los servicios; permitir que fluyan hacia excepciones de negocio (`NoDataFoundException`, `ValidateException`, `GeneralException`).
- Toda respuesta de error debe mantener el formato de `WrapperResponse` con `success: false` y un mensaje claro para el consumidor.

### 5.3 Gestión de Transacciones y Base de Datos
- **Consultas (Lecturas):** Anotar métodos o clases de servicio de solo lectura con `@Transactional(readOnly = true)`. Esto deshabilita el *dirty checking* de Hibernate y optimiza el consumo de memoria.
- **Escrituras:** Métodos de modificación (`create`, `update`, `delete`) deben contar con `@Transactional`.
- **Evolución de Base de Datos:** En entornos productivos y microservicios medianos, se desaconseja `hibernate.ddl-auto: update`. Se recomienda adoptar herramientas de versionado de esquemas como **Flyway** o **Liquibase** (`db/migration/V1__init.sql`).

### 5.4 Mapeo de Objetos y DTOs
- Se cuenta con una abstracción base `GenericMapper<E, D>`.
- En nuevos desarrollos o refactorizaciones, considerar **MapStruct** para reducir código boilerplate y maximizar rendimiento en tiempo de compilación.
- Para DTOs inmutables de consulta, evaluar el uso de `record`:
  ```java
  public record ProductoResumenDTO(Long id, String nombre, BigDecimal precio, Integer stock) {}
  ```

### 5.5 Seguridad y Autenticación
- Arquitectura basada en **JWT Stateless** con filtro `JwtAuthenticationFilter` antes de `UsernamePasswordAuthenticationFilter`.
- Proteger endpoints usando `@PreAuthorize("hasRole('ADMIN')")` o reglas centralizadas en `SecurityConfig`.
- Las contraseñas se cifran obligatoriamente con `BCryptPasswordEncoder` (fuerza por defecto o configurable).
- No almacenar secretos en texto plano en el repositorio; usar variables de entorno (`${JWT_SECRET}`, `${DB_PASSWORD}`).

---

## 6. Consideraciones para Microservicios vs. Aplicaciones de Escritorio

Si este repositorio o módulos derivados conviven con arquitecturas de microservicios o aplicaciones de escritorio:

### A. Para Microservicios Medianos:
- **Observabilidad:** Integrar Spring Boot Actuator (`/actuator/health`, `/actuator/metrics`, `/actuator/info`).
- **Resiliencia:** Definir timeouts explícitos para conexiones de base de datos y llamadas externas HTTP (Spring `RestClient` o `WebClient`).
- **Configuración Externalizada:** Soporte para perfiles (`application-dev.yaml`, `application-prod.yaml`, `application-test.yaml`) y variables de entorno Dockerizadas.
- **Contenedores:** Generar imágenes OCI ligeras y seguras (usando Cloud Native Buildpacks `./gradlew bootBuildImage` o Dockerfiles multi-stage basados en distroless o Alpine con OpenJDK).

### B. Para Clientes de Escritorio (JavaFX / Swing / Modern Desktop):
- **Desacoplamiento:** Consumir esta API a través de clientes HTTP ligeros asíncronos (`java.net.http.HttpClient`).
- **Hilos y UI:** Nunca ejecutar llamadas a la API en el JavaFX Application Thread o EDT de Swing. Usar Virtual Threads de Java 21/25 para despachar llamadas I/O sin congelar la interfaz:
  ```java
  try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      executor.submit(() -> {
          var response = apiClient.fetchProductos();
          Platform.runLater(() -> uiList.setAll(response));
      });
  }
  ```
- **Modularidad y Empaquetado:** Modularizar con `module-info.java` (JPMS) y empaquetar ejecutables nativos mediante `jpackage` o GraalVM Native Image.

---

## 7. Comandos de Desarrollo y Operación

### Compilación y Construcción
```powershell
# Compilar el proyecto y ejecutar verificación
./gradlew build

# Compilar omitiendo pruebas unitarias (desarrollo rápido)
./gradlew build -x test

# Limpiar artefactos generados
./gradlew clean
```

### Ejecución Local
```powershell
# Ejecutar aplicación Spring Boot
./gradlew bootRun

# Ejecutar con perfil específico
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Testing y Calidad
```powershell
# Ejecutar suite de pruebas completa
./gradlew test

# Inspeccionar reporte de pruebas
# Ubicación: build/reports/tests/test/index.html
```

### Acceso a Documentación y Swagger UI
- **URL Base:** `http://localhost:9090`
- **Swagger UI:** `http://localhost:9090/swagger-ui.html`
- **OpenAPI Docs (JSON):** `http://localhost:9090/v3/api-docs`

---

## 8. Directrices para Asistentes AI y Contribuidores

Al implementar nuevas características o resolver incidencias en este repositorio, el asistente de IA y los desarrolladores deben cumplir estrictamente:

1. **Mantener la consistencia arquitectónica:** Toda nueva entidad requiere su `Repository`, `Service` (interfaz e implementación), `DTO`, `Mapper`, `Validator` y `Controller`.
2. **Respetar el contrato de respuesta:** Retornar siempre `WrapperResponse<T>` en los controladores REST.
3. **Validación Temprana (Fail-Fast):** Validar entradas en el controlador (`@Valid`) y reglas de negocio en la capa de servicio/validador (`ValidateException`).
4. **Documentación OpenAPI:** Documentar nuevos endpoints utilizando anotaciones `@Operation`, `@ApiResponse` y `@Tag`.
5. **No introducir dependencias obsoletas:** Utilizar APIs de Java 17/21/25 modernas y evitar librerías deprecadas (ej. `javax.*` en lugar de `jakarta.*`).
6. **Preservar la seguridad:** Nunca desproteger endpoints sin confirmación explícita o justificación de negocio; verificar permisos con `@PreAuthorize`.
7. **Pruebas Automatizadas:** Todo nuevo endpoint o lógica de negocio compleja debe acompañarse de sus pruebas unitarias o de integración con JUnit 5 y Mockito.

---

## 9. Habilidades y Módulos de Conocimiento (Skills)

El asistente debe cargar y aplicar las siguientes habilidades técnicas locales según el contexto de la tarea:

- **Diseño de APIs y Microservicios:**  
  `./.gemini/skills/api-design-reviewer.md`  
  
- **Patrones de uso para Backend:**  
  `./.gemini/skills/backend-patterns.md`  

- **Buenas practica Java para Backend:**  
  `./.gemini/skills/java-coding-standards.md`  
  
- **Arquitectura Microservicios:**  
  `./.gemini/skills/microservices-architect.md`  
 
- **Revision Seguridad:**  
  `./.gemini/skills/security-reviewer.md`  
  
- **Ingenieria en Spring Boot:**  
  `./.gemini/skills/spring-boot-engineer.md`  

- **Patrones de uso Spring Boot:**  
  `./.gemini/skills/springboot-patterns.md`  
  
- **Seguridad Spring Boot:**  
  `./.gemini/skills/springboot-security.md` 