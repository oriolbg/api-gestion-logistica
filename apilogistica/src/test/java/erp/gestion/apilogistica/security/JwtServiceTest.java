package erp.gestion.apilogistica.security;

import erp.gestion.apilogistica.entity.Rol;
import erp.gestion.apilogistica.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests Unitarios - JwtService")
class JwtServiceTest {

    // JwtService tiene @Value para secretKey y jwtExpiration. Para tests unitarios
    // lo instanciamos directamente usando la API de reflexión para inyectar valores.
    private JwtService jwtService;

    private static final String SECRET_KEY = "27b76aaf16e8d087e3d0da8ad983d7d2c1192592224dc5b19c784374ef9cf382";
    private static final String EXPIRATION  = "86400000"; // 24 horas en ms

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        // Inyectar @Value vía reflexión (evita levantar contexto Spring)
        injectField(jwtService, "secretKey", SECRET_KEY);
        injectField(jwtService, "jwtExpiration", EXPIRATION);
    }

    private void injectField(Object target, String fieldName, String value) throws Exception {
        var field = JwtService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private UserDetails buildUserAdmin() {
        Rol rol = Rol.builder().id(1).nombre("ADMIN").build();
        return Usuario.builder()
                .id(1)
                .email("admin@empresa.com")
                .password("$2a$10$hashedPass")
                .activo(true)
                .roles(Set.of(rol))
                .build();
    }

    // =========================================================================
    // generateToken
    // =========================================================================

    @Nested
    @DisplayName("generateToken()")
    class GenerateToken {

        @Test
        @DisplayName("Debe generar un token JWT no nulo ni vacío")
        void debGenerarTokenNoNulo() {
            String token = jwtService.generateToken(buildUserAdmin());
            assertNotNull(token);
            assertFalse(token.isBlank());
        }

        @Test
        @DisplayName("El token generado tiene formato JWT (3 partes separadas por punto)")
        void tokenTieneFormatoJWT() {
            String token = jwtService.generateToken(buildUserAdmin());
            String[] parts = token.split("\\.");
            assertEquals(3, parts.length, "Un JWT debe tener exactamente 3 secciones separadas por '.'");
        }
    }

    // =========================================================================
    // extractUsername
    // =========================================================================

    @Nested
    @DisplayName("extractUsername()")
    class ExtractUsername {

        @Test
        @DisplayName("Debe extraer el email del subject del token")
        void debeExtraerEmailDelToken() {
            UserDetails user = buildUserAdmin();
            String token = jwtService.generateToken(user);

            String username = jwtService.extractUsername(token);

            assertEquals("admin@empresa.com", username);
        }
    }

    // =========================================================================
    // isTokenValid
    // =========================================================================

    @Nested
    @DisplayName("isTokenValid()")
    class IsTokenValid {

        @Test
        @DisplayName("Token generado para el usuario debe ser válido")
        void tokenPropio_EsValido() {
            UserDetails user = buildUserAdmin();
            String token = jwtService.generateToken(user);

            assertTrue(jwtService.isTokenValid(token, user));
        }

        @Test
        @DisplayName("Token de otro usuario NO debe ser válido para el usuario actual")
        void tokenDeOtroUsuario_NoEsValido() {
            UserDetails admin = buildUserAdmin();
            UserDetails otroUsuario = Usuario.builder()
                    .id(2)
                    .email("otro@empresa.com")
                    .password("$2a$10$hashedPass")
                    .activo(true)
                    .build();

            String tokenAdmin = jwtService.generateToken(admin);

            assertFalse(jwtService.isTokenValid(tokenAdmin, otroUsuario));
        }

        @Test
        @DisplayName("Token expirado debe lanzar ExpiredJwtException al intentar parsearlo")
        void tokenExpirado_LanzaExpiredJwtException() throws Exception {
            // Generar un token ya caducado (expiración negativa = en el pasado)
            injectField(jwtService, "jwtExpiration", "-1000");
            UserDetails user = buildUserAdmin();
            String expiredToken = jwtService.generateToken(user);

            // Restaurar expiración normal para otros tests
            injectField(jwtService, "jwtExpiration", "86400000");

            // JJWT 0.13.x lanza ExpiredJwtException al parsear un token caducado.
            // isTokenValid() internamente llama extractUsername() → parseSignedClaims()
            // lo que propaga la excepción. Este es el comportamiento correcto de JJWT.
            assertThrows(
                    io.jsonwebtoken.ExpiredJwtException.class,
                    () -> jwtService.isTokenValid(expiredToken, user),
                    "JJWT debe lanzar ExpiredJwtException al validar un token caducado"
            );
        }
    }
}
