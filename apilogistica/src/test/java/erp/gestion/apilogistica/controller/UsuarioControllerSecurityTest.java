package erp.gestion.apilogistica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import erp.gestion.apilogistica.dto.UsuarioDTO;
import erp.gestion.apilogistica.dto.UsuarioLoginDTO;
import erp.gestion.apilogistica.service.UsuarioService;
import erp.gestion.apilogistica.util.MapperUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests de Integración y Seguridad - UsuarioController (RBAC)")
class UsuarioControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private MapperUtil mapperUtil;

    @Test
    @DisplayName("GET /api/usuarios sin token/autenticación debe ser rechazado (401 o 403)")
    void findAll_SinAutenticacion_Rechazado() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /api/usuarios con rol 'USER' debe ser denegado con 403 Forbidden")
    void findAll_RolUser_AccesoDenegado() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/usuarios con rol 'ADMIN' debe permitir acceso con 200 OK")
    void findAll_RolAdmin_AccesoPermitido() throws Exception {
        Page<UsuarioDTO> paginaVacia = new PageImpl<>(List.of());
        when(usuarioService.findAll(any(), any())).thenReturn(paginaVacia);

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/usuarios debe ser público para registro sin requerir autenticación (201 Created)")
    void create_EndpointPublico_PermitidoSinAuth() throws Exception {
        UsuarioDTO request = UsuarioDTO.builder()
                .email("nuevo@empresa.com")
                .password("Password123")
                .activo(true)
                .build();

        UsuarioLoginDTO loginDTO = UsuarioLoginDTO.builder()
                .id(1)
                .email("nuevo@empresa.com")
                .build();

        when(usuarioService.create(any(UsuarioDTO.class))).thenReturn(request);
        when(mapperUtil.secureCreateUserLogin(any())).thenReturn(loginDTO);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[?(@.ok == true || @.success == true)]").exists())
                .andExpect(jsonPath("$.data.email").value("nuevo@empresa.com"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("DELETE /api/usuarios/{id} con rol 'USER' debe ser denegado con 403 Forbidden")
    void delete_RolUser_AccesoDenegado() throws Exception {
        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/usuarios/{id} con rol 'ADMIN' debe permitir la eliminación con 200 OK")
    void delete_RolAdmin_AccesoPermitido() throws Exception {
        doNothing().when(usuarioService).delete(1);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
