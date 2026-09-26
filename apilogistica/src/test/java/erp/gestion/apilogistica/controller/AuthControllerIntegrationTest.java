package erp.gestion.apilogistica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import erp.gestion.apilogistica.dto.AuthResponseDTO;
import erp.gestion.apilogistica.dto.LoginRequestDTO;
import erp.gestion.apilogistica.exception.NoDataFoundException;
import erp.gestion.apilogistica.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para AuthController.
 *
 * El endpoint POST /api/auth/login es público (no requiere autenticación).
 * Se valida: respuesta en login exitoso, credenciales incorrectas y usuario inexistente.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests de Integración - AuthController")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthService authService;

    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {

        @Test
        @DisplayName("Login exitoso debe retornar 200 OK con token JWT")
        void loginExitoso_RetornaToken() throws Exception {
            LoginRequestDTO request = new LoginRequestDTO("admin@empresa.com", "Password123");

            AuthResponseDTO response = AuthResponseDTO.builder()
                    .id(1)
                    .email("admin@empresa.com")
                    .token("eyJhbGciOiJIUzI1NiJ9.mocked.token")
                    .build();

            when(authService.login(any(LoginRequestDTO.class))).thenReturn(response);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.ok").value(true))
                    .andExpect(jsonPath("$.message").value("success"))
                    .andExpect(jsonPath("$.data.token").value("eyJhbGciOiJIUzI1NiJ9.mocked.token"))
                    .andExpect(jsonPath("$.data.email").value("admin@empresa.com"))
                    .andExpect(jsonPath("$.data.id").value(1));
        }

        @Test
        @DisplayName("Login con credenciales incorrectas debe retornar 401 Unauthorized")
        void loginCredencialesInvalidas_Unauthorized() throws Exception {
            LoginRequestDTO request = new LoginRequestDTO("admin@empresa.com", "wrong");

            when(authService.login(any(LoginRequestDTO.class)))
                    .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.ok").value(false));
        }

        @Test
        @DisplayName("Login con usuario no encontrado en BD debe retornar 400 Bad Request")
        void loginUsuarioInexistente_BadRequest() throws Exception {
            LoginRequestDTO request = new LoginRequestDTO("fantasma@empresa.com", "Pass123");

            when(authService.login(any(LoginRequestDTO.class)))
                    .thenThrow(new NoDataFoundException("Usuario no encontrado"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.ok").value(false))
                    .andExpect(jsonPath("$.message").value("Usuario no encontrado"));
        }

        @Test
        @DisplayName("Endpoint /api/auth/login es público y no requiere token (sin 401/403)")
        void loginEsPublico_SinToken_Accesible() throws Exception {
            // El mock lanza excepción de negocio, pero no 401/403 de Spring Security
            when(authService.login(any())).thenThrow(new NoDataFoundException("x"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    // No debe ser 401 ni 403 por falta de autenticación,
                    // sino 400 por la lógica de negocio.
                    .andExpect(status().isBadRequest());
        }
    }
}
