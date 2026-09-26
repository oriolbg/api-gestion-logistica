package erp.gestion.apilogistica.testcontainers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Clase base abstracta para tests de integración real con Testcontainers (MySQL).
 *
 * <p><b>Patrón Singleton Container</b>: el contenedor MySQL se declara como {@code static}
 * y {@code @Container}, por lo que JUnit 5 + Testcontainers lo inicia una sola vez
 * para todos los tests de la suite, minimizando el overhead de arranque de Docker.
 *
 * <p><b>Uso</b>: extender esta clase en cualquier test de integración que requiera
 * una base de datos MySQL real. No es necesario añadir ninguna anotación extra;
 * {@code @Testcontainers} y {@code @ActiveProfiles("test")} ya están incluidos aquí.
 *
 * <pre>{@code
 * @SpringBootTest
 * class MiRepositoryIntegrationTest extends AbstractMySQLContainerTest {
 *     @Autowired
 *     private MiRepository repository;
 *
 *     @Test
 *     void debeGuardarEntidad() { ... }
 * }
 * }</pre>
 */
@Testcontainers
public abstract class AbstractMySQLContainerTest {

    /**
     * Contenedor MySQL 8.x compartido por todos los tests que extiendan esta clase.
     * La imagen oficial {@code mysql:8.0} garantiza compatibilidad con el dialecto
     * {@code org.hibernate.dialect.MySQLDialect} configurado en el proyecto.
     */
    @Container
    static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("gestion_logistica_test")
            .withUsername("root")
            .withPassword("root")
            // Optimizaciones de arranque para CI: desactiva logs binarios y fsync
            .withCommand("--character-set-server=utf8mb4",
                         "--collation-server=utf8mb4_unicode_ci",
                         "--skip-log-bin");

    /**
     * Registra dinámicamente las propiedades de conexión de Spring al contenedor MySQL
     * iniciado por Testcontainers, sobreescribiendo los valores del {@code application-test.yaml}.
     *
     * <p>Este método es invocado por Spring antes de crear el {@code ApplicationContext}.
     */
    @DynamicPropertySource
    static void configurarPropiedadesMySQL(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",       MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username",  MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password",  MY_SQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
    }
}
