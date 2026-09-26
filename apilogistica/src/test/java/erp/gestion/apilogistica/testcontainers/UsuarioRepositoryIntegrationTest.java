package erp.gestion.apilogistica.testcontainers;

import erp.gestion.apilogistica.entity.Rol;
import erp.gestion.apilogistica.entity.Usuario;
import erp.gestion.apilogistica.repository.RolRepository;
import erp.gestion.apilogistica.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de integración real contra MySQL (Testcontainers).
 *
 * <p>Verifica el comportamiento del repositorio JPA {@link UsuarioRepository}
 * contra una base de datos MySQL real, incluyendo consultas derivadas y relaciones N:M con Rol.
 *
 * <p>Hereda de {@link AbstractMySQLContainerTest} que configura el contenedor Docker
 * y registra la URL JDBC dinámica. Usa el perfil {@code test} con {@code ddl-auto=create-drop}.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional   // Cada test tiene su propia transacción que se revierte al final
@DisplayName("Tests de Integración (Testcontainers/MySQL) - UsuarioRepository")
class UsuarioRepositoryIntegrationTest extends AbstractMySQLContainerTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    private Rol rolAdmin;
    private Rol rolUser;

    /**
     * Limpia y repuebla los datos antes de cada test.
     * @Transactional garantiza el rollback automático al finalizar.
     */
    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();

        rolAdmin = rolRepository.save(Rol.builder().nombre("ADMIN").build());
        rolUser  = rolRepository.save(Rol.builder().nombre("USER").build());
    }

    // =========================================================================
    // Contenedor arranca correctamente
    // =========================================================================

    @Test
    @DisplayName("El contenedor MySQL debe estar corriendo")
    void contenedorMysql_EstaActivo() {
        assertTrue(MY_SQL_CONTAINER.isRunning(),
                "El contenedor MySQL de Testcontainers debe estar activo");
    }

    // =========================================================================
    // findByEmail
    // =========================================================================

    @Nested
    @DisplayName("findByEmail()")
    class FindByEmail {

        @Test
        @DisplayName("Debe encontrar usuario por email exacto")
        void emailExistente_RetornaUsuario() {
            usuarioRepository.save(Usuario.builder()
                    .email("admin@empresa.com")
                    .password("$2a$10$hash")
                    .activo(true)
                    .build());

            Optional<Usuario> result = usuarioRepository.findByEmail("admin@empresa.com");

            assertTrue(result.isPresent());
            assertEquals("admin@empresa.com", result.get().getEmail());
        }

        @Test
        @DisplayName("Debe retornar vacío si el email no existe")
        void emailInexistente_RetornaVacio() {
            Optional<Usuario> result = usuarioRepository.findByEmail("noexiste@empresa.com");
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("La búsqueda es sensible a mayúsculas (email como campo único)")
        void emailConDiferenteCaso_NoEncuentra() {
            usuarioRepository.save(Usuario.builder()
                    .email("admin@empresa.com")
                    .password("$2a$10$hash")
                    .activo(true)
                    .build());

            // findByEmail es exacto (no ignoreCase), a diferencia de findByEmailContainingIgnoreCase
            Optional<Usuario> resultMayuscula = usuarioRepository.findByEmail("ADMIN@empresa.com");
            assertTrue(resultMayuscula.isEmpty(),
                    "findByEmail debe ser sensible a mayúsculas");
        }
    }

    // =========================================================================
    // findByEmailContainingIgnoreCase
    // =========================================================================

    @Nested
    @DisplayName("findByEmailContainingIgnoreCase()")
    class FindByEmailContaining {

        @Test
        @DisplayName("Debe encontrar usuarios cuyo email contenga el término (ignorando caso)")
        void termino_MatchParcial_RetornaCoincidencias() {
            usuarioRepository.save(Usuario.builder().email("admin@empresa.com")
                    .password("$2a$10$h").activo(true).build());
            usuarioRepository.save(Usuario.builder().email("user@empresa.com")
                    .password("$2a$10$h").activo(true).build());
            usuarioRepository.save(Usuario.builder().email("otro@otro.com")
                    .password("$2a$10$h").activo(true).build());

            Page<Usuario> result = usuarioRepository.findByEmailContainingIgnoreCase(
                    PageRequest.of(0, 10), "empresa");

            assertEquals(2, result.getTotalElements());
        }

        @Test
        @DisplayName("Búsqueda insensible a mayúsculas: 'ADMIN' debe coincidir con 'admin@empresa.com'")
        void terminoMayuscula_MatchIgnoreCase() {
            usuarioRepository.save(Usuario.builder().email("admin@empresa.com")
                    .password("$2a$10$h").activo(true).build());

            Page<Usuario> result = usuarioRepository.findByEmailContainingIgnoreCase(
                    PageRequest.of(0, 10), "ADMIN");

            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Sin coincidencias debe retornar página vacía")
        void sinCoincidencias_PaginaVacia() {
            usuarioRepository.save(Usuario.builder().email("admin@empresa.com")
                    .password("$2a$10$h").activo(true).build());

            Page<Usuario> result = usuarioRepository.findByEmailContainingIgnoreCase(
                    PageRequest.of(0, 10), "xyz_no_existe");

            assertEquals(0, result.getTotalElements());
        }
    }

    // =========================================================================
    // Relación N:M con Rol
    // =========================================================================

    @Nested
    @DisplayName("Relación N:M Usuario ↔ Rol")
    class RelacionConRol {

        @Test
        @DisplayName("Debe persistir usuario con roles ADMIN y USER y recuperarlos")
        void usuarioConRoles_PersistidoYRecuperado() {
            Usuario usuario = Usuario.builder()
                    .email("multi@empresa.com")
                    .password("$2a$10$hash")
                    .activo(true)
                    .roles(Set.of(rolAdmin, rolUser))
                    .build();

            Usuario saved = usuarioRepository.save(usuario);

            Optional<Usuario> found = usuarioRepository.findById(saved.getId());
            assertTrue(found.isPresent());
            assertEquals(2, found.get().getRoles().size());
            assertTrue(found.get().getRoles().stream()
                    .anyMatch(r -> r.getNombre().equals("ADMIN")));
        }

        @Test
        @DisplayName("getAuthorities() debe retornar las authorities con prefijo 'ROLE_'")
        void getAuthorities_RetornaConPrefijoROLE() {
            Usuario usuario = usuarioRepository.save(Usuario.builder()
                    .email("auth@empresa.com")
                    .password("$2a$10$hash")
                    .activo(true)
                    .roles(Set.of(rolAdmin))
                    .build());

            Usuario found = usuarioRepository.findById(usuario.getId()).orElseThrow();

            assertTrue(found.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        }
    }

    // =========================================================================
    // Paginación
    // =========================================================================

    @Nested
    @DisplayName("Paginación")
    class Paginacion {

        @Test
        @DisplayName("Debe respetar el límite de página solicitado")
        void paginacion_RespetaLimite() {
            for (int i = 1; i <= 7; i++) {
                usuarioRepository.save(Usuario.builder()
                        .email("user" + i + "@empresa.com")
                        .password("$2a$10$hash")
                        .activo(true)
                        .build());
            }

            Page<Usuario> primeraPage = usuarioRepository.findAll(PageRequest.of(0, 3));
            Page<Usuario> segundaPage = usuarioRepository.findAll(PageRequest.of(1, 3));

            assertEquals(3, primeraPage.getNumberOfElements());
            assertEquals(3, segundaPage.getNumberOfElements());
            assertEquals(7, primeraPage.getTotalElements());
            assertEquals(3, primeraPage.getTotalPages()); // 7 / 3 = ceil = 3
        }
    }
}
