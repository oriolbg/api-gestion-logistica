package erp.gestion.apilogistica.service;

import erp.gestion.apilogistica.dto.UsuarioDTO;
import erp.gestion.apilogistica.entity.Usuario;
import erp.gestion.apilogistica.exception.NoDataFoundException;
import erp.gestion.apilogistica.exception.ValidateException;
import erp.gestion.apilogistica.mapper.UsuarioMapper;
import erp.gestion.apilogistica.repository.UsuarioRepository;
import erp.gestion.apilogistica.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - UsuarioServiceImpl")
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private UsuarioMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl service;

    // =========================================================================
    // findAll
    // =========================================================================

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("Sin búsqueda retorna todos los usuarios paginados")
        void sinBusqueda_RetornaTodos() {
            Pageable pageable = PageRequest.of(0, 5);
            Usuario usuario = Usuario.builder().id(1).email("a@b.com").activo(true).build();
            Page<Usuario> pageEntidad = new PageImpl<>(List.of(usuario));
            UsuarioDTO dto = UsuarioDTO.builder().id(1).email("a@b.com").build();

            when(repository.findAll(pageable)).thenReturn(pageEntidad);
            when(mapper.toDTO(usuario)).thenReturn(dto);

            Page<UsuarioDTO> result = service.findAll(pageable, null);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(repository).findAll(pageable);
            verify(repository, never()).findByEmailContainingIgnoreCase(any(), any());
        }

        @Test
        @DisplayName("Con búsqueda no vacía delega en findByEmailContainingIgnoreCase")
        void conBusqueda_DelegaEnQuery() {
            Pageable pageable = PageRequest.of(0, 5);
            String search = "admin";
            Page<Usuario> pageVacia = new PageImpl<>(List.of());

            when(repository.findByEmailContainingIgnoreCase(pageable, search)).thenReturn(pageVacia);

            Page<UsuarioDTO> result = service.findAll(pageable, search);

            assertNotNull(result);
            verify(repository).findByEmailContainingIgnoreCase(pageable, search);
            verify(repository, never()).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Con búsqueda en blanco (solo espacios) retorna todos los registros")
        void conBusquedaBlanca_TrataComoNula() {
            Pageable pageable = PageRequest.of(0, 5);
            when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));

            service.findAll(pageable, "   ");

            verify(repository).findAll(pageable);
        }
    }

    // =========================================================================
    // findById
    // =========================================================================

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("Debe retornar DTO cuando el usuario existe")
        void usuarioExistente_RetornaDTO() {
            Usuario usuario = Usuario.builder().id(1).email("a@b.com").activo(true).build();
            UsuarioDTO dto = UsuarioDTO.builder().id(1).email("a@b.com").build();

            when(repository.findById(1)).thenReturn(Optional.of(usuario));
            when(mapper.toDTO(usuario)).thenReturn(dto);

            UsuarioDTO result = service.findById(1);

            assertNotNull(result);
            assertEquals("a@b.com", result.getEmail());
        }

        @Test
        @DisplayName("Debe lanzar NoDataFoundException si el usuario no existe")
        void usuarioInexistente_LanzaExcepcion() {
            when(repository.findById(99)).thenReturn(Optional.empty());

            assertThrows(NoDataFoundException.class, () -> service.findById(99));
        }
    }

    // =========================================================================
    // create
    // =========================================================================

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("Debe crear usuario con contraseña cifrada cuando el email no está registrado")
        void emailNuevo_CreaUsuarioCifrado() {
            UsuarioDTO dto = UsuarioDTO.builder()
                    .email("nuevo@empresa.com")
                    .password("Password123")
                    .activo(true)
                    .build();

            Usuario entidad = Usuario.builder().email("nuevo@empresa.com").build();
            Usuario saved = Usuario.builder().id(10).email("nuevo@empresa.com").build();
            UsuarioDTO dtoResultado = UsuarioDTO.builder().id(10).email("nuevo@empresa.com").build();

            when(repository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
            when(mapper.toEntity(dto)).thenReturn(entidad);
            when(passwordEncoder.encode("Password123")).thenReturn("$2a$10$hashedPassword");
            when(repository.save(entidad)).thenReturn(saved);
            when(mapper.toDTO(saved)).thenReturn(dtoResultado);

            UsuarioDTO result = service.create(dto);

            assertNotNull(result);
            assertEquals(10, result.getId());
            // Verifica que el password se encripta antes de guardar
            assertEquals("$2a$10$hashedPassword", entidad.getPassword());
            verify(passwordEncoder).encode("Password123");
            verify(repository).save(entidad);
        }

        @Test
        @DisplayName("Debe lanzar ValidateException si el email ya existe")
        void emailDuplicado_LanzaValidateException() {
            UsuarioDTO dto = UsuarioDTO.builder()
                    .email("existente@empresa.com")
                    .password("Pass123")
                    .build();

            when(repository.findByEmail(dto.getEmail()))
                    .thenReturn(Optional.of(Usuario.builder().email("existente@empresa.com").build()));

            ValidateException ex = assertThrows(ValidateException.class, () -> service.create(dto));

            assertEquals("El email ya está registrado", ex.getMessage());
            verify(repository, never()).save(any());
            verify(passwordEncoder, never()).encode(anyString());
        }
    }

    // =========================================================================
    // update
    // =========================================================================

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("Debe actualizar y re-cifrar contraseña si se proporciona una nueva")
        void conNuevoPassword_ActualizaYCifra() {
            Integer id = 1;
            UsuarioDTO dto = UsuarioDTO.builder()
                    .email("a@b.com")
                    .password("NuevoPass456")
                    .build();

            Usuario entidad = Usuario.builder().id(id).email("a@b.com")
                    .password("$2a$10$oldHash").activo(true).build();

            when(repository.findById(id)).thenReturn(Optional.of(entidad));
            when(passwordEncoder.encode("NuevoPass456")).thenReturn("$2a$10$newHash");
            when(repository.save(entidad)).thenReturn(entidad);
            when(mapper.toDTO(entidad)).thenReturn(dto);

            service.update(id, dto);

            assertEquals("$2a$10$newHash", entidad.getPassword());
            verify(passwordEncoder).encode("NuevoPass456");
        }

        @Test
        @DisplayName("Debe actualizar sin re-cifrar si el password viene vacío")
        void sinNuevoPassword_NoCifra() {
            Integer id = 1;
            UsuarioDTO dto = UsuarioDTO.builder().email("a@b.com").password("").build();
            Usuario entidad = Usuario.builder().id(id).email("a@b.com")
                    .password("$2a$10$oldHash").activo(true).build();

            when(repository.findById(id)).thenReturn(Optional.of(entidad));
            when(repository.save(entidad)).thenReturn(entidad);
            when(mapper.toDTO(entidad)).thenReturn(dto);

            service.update(id, dto);

            // El password no debe cambiar
            assertEquals("$2a$10$oldHash", entidad.getPassword());
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        @DisplayName("Debe lanzar NoDataFoundException si el ID no existe")
        void idInexistente_LanzaExcepcion() {
            when(repository.findById(999)).thenReturn(Optional.empty());

            assertThrows(NoDataFoundException.class,
                    () -> service.update(999, UsuarioDTO.builder().build()));
        }
    }

    // =========================================================================
    // delete
    // =========================================================================

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("Debe eliminar cuando el usuario existe")
        void usuarioExistente_EliminaCorrectamente() {
            when(repository.existsById(1)).thenReturn(true);

            assertDoesNotThrow(() -> service.delete(1));
            verify(repository).deleteById(1);
        }

        @Test
        @DisplayName("Debe lanzar NoDataFoundException si el usuario no existe")
        void usuarioInexistente_LanzaExcepcion() {
            when(repository.existsById(99)).thenReturn(false);

            assertThrows(NoDataFoundException.class, () -> service.delete(99));
            verify(repository, never()).deleteById(any());
        }
    }
}
