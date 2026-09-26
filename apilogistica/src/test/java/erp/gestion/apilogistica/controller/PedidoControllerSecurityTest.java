package erp.gestion.apilogistica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import erp.gestion.apilogistica.dto.PedidoDTO;
import erp.gestion.apilogistica.dto.PedidoDetalleDTO;
import erp.gestion.apilogistica.exception.NoDataFoundException;
import erp.gestion.apilogistica.service.PedidoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración + seguridad para PedidoController.
 *
 * Estrategia:
 *  - @SpringBootTest levanta el contexto completo (con filtro JWT y SecurityFilterChain reales).
 *  - @MockitoBean reemplaza PedidoService para aislar la capa HTTP de la lógica de negocio.
 *  - @WithMockUser simula usuarios autenticados con roles específicos sin necesidad de JWT real.
 *  - El controlador está protegido con @PreAuthorize("hasAnyRole('ADMIN')") en clase.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests de Integración y Seguridad - PedidoController (RBAC)")
class PedidoControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PedidoService pedidoService;

    // ===========================================================================
    // Helpers: builders de DTOs reutilizables
    // ===========================================================================

    private PedidoDetalleDTO buildDetalle(int cantidad, String precio) {
        BigDecimal precioUnitario = new BigDecimal(precio);
        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        return PedidoDetalleDTO.builder()
                .productoId(1L)
                .cantidad(cantidad)
                .precioUnitario(precioUnitario)
                .subtotal(subtotal)
                .build();
    }

    private PedidoDTO buildPedidoDTO() {
        return PedidoDTO.builder()
                .clienteId(1L)
                .tipoComprobanteCodigo("FAC")
                .serie("F001")
                .correlativo(1)
                .detalles(List.of(buildDetalle(2, "50.00")))
                .build();
    }

    private PedidoDTO buildPedidoDTORespuesta() {
        return PedidoDTO.builder()
                .id(1L)
                .clienteId(1L)
                .tipoComprobanteCodigo("FAC")
                .serie("F001")
                .correlativo(1)
                .fecha(LocalDate.now())
                .total(new BigDecimal("100.00"))
                .detalles(List.of(buildDetalle(2, "50.00")))
                .build();
    }

    // ===========================================================================
    // GET /api/pedidos — findAll
    // ===========================================================================

    @Nested
    @DisplayName("GET /api/pedidos")
    class FindAll {

        @Test
        @DisplayName("Sin autenticación debe retornar 403 Forbidden")
        void sinAuth_Forbidden() throws Exception {
            mockMvc.perform(get("/api/pedidos"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Con rol USER debe retornar 403 Forbidden (solo ADMIN tiene acceso)")
        void rolUser_Forbidden() throws Exception {
            mockMvc.perform(get("/api/pedidos"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Con rol ADMIN debe retornar 200 OK con lista paginada")
        void rolAdmin_Ok() throws Exception {
            Page<PedidoDTO> paginaVacia = new PageImpl<>(List.of(buildPedidoDTORespuesta()));
            when(pedidoService.findAll(any(), isNull())).thenReturn(paginaVacia);

            mockMvc.perform(get("/api/pedidos"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.ok").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.totalElements").value(1));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Con parámetro search retorna resultados filtrados")
        void conParametroSearch_RetornaFiltrado() throws Exception {
            Page<PedidoDTO> pagina = new PageImpl<>(List.of());
            when(pedidoService.findAll(any(), eq("F001"))).thenReturn(pagina);

            mockMvc.perform(get("/api/pedidos").param("search", "F001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.ok").value(true));
        }
    }

    // ===========================================================================
    // GET /api/pedidos/{id} — findById
    // ===========================================================================

    @Nested
    @DisplayName("GET /api/pedidos/{id}")
    class FindById {

        @Test
        @DisplayName("Sin autenticación debe retornar 403 Forbidden")
        void sinAuth_Forbidden() throws Exception {
            mockMvc.perform(get("/api/pedidos/1"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Con rol ADMIN y pedido existente debe retornar 200 OK con datos")
        void rolAdmin_PedidoExistente_Ok() throws Exception {
            when(pedidoService.findById(1L)).thenReturn(buildPedidoDTORespuesta());

            mockMvc.perform(get("/api/pedidos/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.ok").value(true))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.total").value(100.00));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Con rol ADMIN y pedido inexistente debe retornar 400 con mensaje de error")
        void rolAdmin_PedidoInexistente_BadRequest() throws Exception {
            when(pedidoService.findById(999L))
                    .thenThrow(new NoDataFoundException("No existe un registro con ese ID"));

            mockMvc.perform(get("/api/pedidos/999"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.ok").value(false))
                    .andExpect(jsonPath("$.message").value("No existe un registro con ese ID"));
        }
    }

    // ===========================================================================
    // POST /api/pedidos — create
    // ===========================================================================

    @Nested
    @DisplayName("POST /api/pedidos")
    class Create {

        @Test
        @DisplayName("Sin autenticación debe retornar 403 Forbidden")
        void sinAuth_Forbidden() throws Exception {
            mockMvc.perform(post("/api/pedidos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(buildPedidoDTO())))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Con rol USER debe retornar 403 Forbidden")
        void rolUser_Forbidden() throws Exception {
            mockMvc.perform(post("/api/pedidos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(buildPedidoDTO())))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Con rol ADMIN y payload válido debe retornar 201 Created")
        void rolAdmin_PayloadValido_Created() throws Exception {
            when(pedidoService.create(any(PedidoDTO.class))).thenReturn(buildPedidoDTORespuesta());

            mockMvc.perform(post("/api/pedidos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(buildPedidoDTO())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.ok").value(true))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.total").value(100.00));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Con payload sin detalles debe retornar 400 Bad Request (validación Bean)")
        void rolAdmin_SinDetalles_BadRequest() throws Exception {
            PedidoDTO sinDetalles = PedidoDTO.builder()
                    .clienteId(1L)
                    .tipoComprobanteCodigo("FAC")
                    .serie("F001")
                    .correlativo(1)
                    .detalles(List.of()) // Lista vacía → @NotEmpty falla
                    .build();

            mockMvc.perform(post("/api/pedidos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sinDetalles)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.ok").value(false));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Con payload sin serie (campo @NotBlank) debe retornar 400 Bad Request")
        void rolAdmin_SinSerie_BadRequest() throws Exception {
            PedidoDTO sinSerie = buildPedidoDTO();
            sinSerie.setSerie(null); // @NotBlank dispara

            mockMvc.perform(post("/api/pedidos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sinSerie)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.ok").value(false));
        }
    }

    // ===========================================================================
    // PUT /api/pedidos/{id} — update
    // ===========================================================================

    @Nested
    @DisplayName("PUT /api/pedidos/{id}")
    class Update {

        @Test
        @DisplayName("Sin autenticación debe retornar 403 Forbidden")
        void sinAuth_Forbidden() throws Exception {
            mockMvc.perform(put("/api/pedidos/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(buildPedidoDTO())))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Con rol ADMIN y pedido existente debe retornar 200 OK")
        void rolAdmin_Actualiza_Ok() throws Exception {
            when(pedidoService.update(eq(1L), any(PedidoDTO.class)))
                    .thenReturn(buildPedidoDTORespuesta());

            mockMvc.perform(put("/api/pedidos/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(buildPedidoDTO())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.ok").value(true))
                    .andExpect(jsonPath("$.data.total").value(100.00));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Con pedido inexistente debe retornar 400 Bad Request")
        void rolAdmin_PedidoInexistente_BadRequest() throws Exception {
            when(pedidoService.update(eq(999L), any(PedidoDTO.class)))
                    .thenThrow(new NoDataFoundException("No existe un registro con ese ID"));

            mockMvc.perform(put("/api/pedidos/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(buildPedidoDTO())))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.ok").value(false));
        }
    }

    // ===========================================================================
    // DELETE /api/pedidos/{id} — delete
    // ===========================================================================

    @Nested
    @DisplayName("DELETE /api/pedidos/{id}")
    class Delete {

        @Test
        @DisplayName("Sin autenticación debe retornar 403 Forbidden")
        void sinAuth_Forbidden() throws Exception {
            mockMvc.perform(delete("/api/pedidos/1"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Con rol USER debe retornar 403 Forbidden")
        void rolUser_Forbidden() throws Exception {
            mockMvc.perform(delete("/api/pedidos/1"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Con rol ADMIN debe eliminar correctamente y retornar 200 OK")
        void rolAdmin_Elimina_Ok() throws Exception {
            doNothing().when(pedidoService).delete(1L);

            mockMvc.perform(delete("/api/pedidos/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.ok").value(true));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Con pedido inexistente debe retornar 400 con mensaje de error")
        void rolAdmin_PedidoInexistente_BadRequest() throws Exception {
            doThrow(new NoDataFoundException("No existe un registro con ese ID."))
                    .when(pedidoService).delete(999L);

            mockMvc.perform(delete("/api/pedidos/999"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.ok").value(false))
                    .andExpect(jsonPath("$.message").value("No existe un registro con ese ID."));
        }
    }
}
