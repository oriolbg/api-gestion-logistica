package erp.gestion.apilogistica.service;


import erp.gestion.apilogistica.dto.AuthResponseDTO;
import erp.gestion.apilogistica.dto.LoginRequestDTO;
import erp.gestion.apilogistica.entity.Usuario;
import erp.gestion.apilogistica.exception.NoDataFoundException;
import erp.gestion.apilogistica.repository.UsuarioRepository;
import erp.gestion.apilogistica.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
	
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NoDataFoundException("Usuario no encontrado"));

        String jwtToken = jwtService.generateToken(usuario);

        return AuthResponseDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .token(jwtToken)
                .build();
    }
}
