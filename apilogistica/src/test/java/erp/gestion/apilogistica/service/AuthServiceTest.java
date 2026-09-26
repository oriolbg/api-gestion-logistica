package erp.gestion.apilogistica.service;

import erp.gestion.apilogistica.dto.AuthResponseDTO;
import erp.gestion.apilogistica.dto.LoginRequestDTO;
import erp.gestion.apilogistica.entity.Usuario;
import erp.gestion.apilogistica.exception.NoDataFoundException;
import erp.gestion.apilogistica.repository.UsuarioRepository;
import erp.gestion.apilogistica.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - AuthService")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Debe autenticar correctamente y retornar token JWT en login exitoso")
    void login_CredencialesValidas_RetornaToken() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("admin@empresa.com", "admin123");
        Usuario usuario = Usuario.builder()
                .id(1)
                .email("admin@empresa.com")
                .password("encoded_hash")
                .activo(true)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(usuario)).thenReturn("mocked-jwt-token-12345");

        // Act
        AuthResponseDTO response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("admin@empresa.com", response.getEmail());
        assertEquals("mocked-jwt-token-12345", response.getToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepository).findByEmail(request.getEmail());
        verify(jwtService).generateToken(usuario);
    }

    @Test
    @DisplayName("Debe fallar con BadCredentialsException si la contraseña o email son incorrectos")
    void login_CredencialesInvalidas_LanzaBadCredentialsException() {
        LoginRequestDTO request = new LoginRequestDTO("admin@empresa.com", "wrong_password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales incorrectas"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(usuarioRepository, never()).findByEmail(anyString());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Debe lanzar NoDataFoundException si el usuario no existe en BD a pesar de autenticarse")
    void login_UsuarioNoExisteEnBD_LanzaNoDataFoundException() {
        LoginRequestDTO request = new LoginRequestDTO("fantasma@empresa.com", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(NoDataFoundException.class, () -> authService.login(request));

        verify(jwtService, never()).generateToken(any());
    }
}
