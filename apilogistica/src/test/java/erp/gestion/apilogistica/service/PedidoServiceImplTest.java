package erp.gestion.apilogistica.service;

import erp.gestion.apilogistica.dto.PedidoDTO;
import erp.gestion.apilogistica.dto.PedidoDetalleDTO;
import erp.gestion.apilogistica.entity.Pedido;
import erp.gestion.apilogistica.exception.NoDataFoundException;
import erp.gestion.apilogistica.mapper.PedidoMapper;
import erp.gestion.apilogistica.repository.PedidoRepository;
import erp.gestion.apilogistica.service.impl.PedidoServiceImpl;
import erp.gestion.apilogistica.validator.PedidoValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - PedidoServiceImpl")
class PedidoServiceImplTest {

    @Mock
    private PedidoRepository repository;

    @Mock
    private PedidoMapper mapper;

    @Mock
    private PedidoValidator pedidoValidator;

    @InjectMocks
    private PedidoServiceImpl service;

    @Test
    @DisplayName("Debe crear un pedido calculando el total correctamente y asignando la fecha de hoy")
    void create_DebeCalcularTotalYAsignarFecha() {
        // Arrange
        PedidoDetalleDTO detalle1 = PedidoDetalleDTO.builder()
                .cantidad(2)
                .precioUnitario(new BigDecimal("50.00"))
                .subtotal(new BigDecimal("100.00"))
                .build();
        PedidoDetalleDTO detalle2 = PedidoDetalleDTO.builder()
                .cantidad(3)
                .precioUnitario(new BigDecimal("20.00"))
                .subtotal(new BigDecimal("60.00"))
                .build();

        PedidoDTO inputDto = PedidoDTO.builder()
                .detalles(List.of(detalle1, detalle2))
                .build();

        Pedido entityMock = new Pedido();
        entityMock.setDetalles(new ArrayList<>());

        Pedido savedMock = new Pedido();
        savedMock.setId(10L);
        savedMock.setTotal(new BigDecimal("160.00"));
        savedMock.setFecha(LocalDate.now());

        PedidoDTO outputDto = PedidoDTO.builder()
                .id(10L)
                .total(new BigDecimal("160.00"))
                .fecha(LocalDate.now())
                .build();

        when(mapper.toEntity(inputDto)).thenReturn(entityMock);
        when(repository.save(any(Pedido.class))).thenReturn(savedMock);
        when(mapper.toDTO(savedMock)).thenReturn(outputDto);

        // Act
        PedidoDTO result = service.create(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(new BigDecimal("160.00"), result.getTotal());
        assertEquals(LocalDate.now(), result.getFecha());

        // Verificar que el total calculado y asignado a la entidad fue 160.00
        assertEquals(new BigDecimal("160.00"), entityMock.getTotal());
        assertEquals(LocalDate.now(), entityMock.getFecha());

        verify(pedidoValidator).validarSubtotalesDetalle(inputDto);
        verify(repository).save(entityMock);
    }

    @Test
    @DisplayName("Debe actualizar un pedido existente recalculando el total y delegando en updateEntityFromDto")
    void update_PedidoExistente_DebeRecalcularTotalYGuardar() {
        // Arrange
        Long pedidoId = 5L;
        Pedido pedidoExistente = Pedido.builder()
                .id(pedidoId)
                .fecha(LocalDate.of(2025, 1, 1)) // Fecha original que no debe mutar
                .total(new BigDecimal("50.00"))
                .build();

        PedidoDetalleDTO detalleNuevo = PedidoDetalleDTO.builder()
                .cantidad(4)
                .precioUnitario(new BigDecimal("25.00"))
                .subtotal(new BigDecimal("100.00"))
                .build();

        PedidoDTO dtoActualizado = PedidoDTO.builder()
                .id(pedidoId)
                .detalles(List.of(detalleNuevo))
                .build();

        PedidoDTO dtoResultado = PedidoDTO.builder()
                .id(pedidoId)
                .total(new BigDecimal("100.00"))
                .build();

        when(repository.findById(pedidoId)).thenReturn(Optional.of(pedidoExistente));
        when(repository.save(pedidoExistente)).thenReturn(pedidoExistente);
        when(mapper.toDTO(pedidoExistente)).thenReturn(dtoResultado);

        // Act
        PedidoDTO result = service.update(pedidoId, dtoActualizado);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getTotal());
        assertEquals(new BigDecimal("100.00"), pedidoExistente.getTotal());

        verify(pedidoValidator).validarSubtotalesDetalle(dtoActualizado);
        verify(mapper).updateEntityFromDto(dtoActualizado, pedidoExistente);
        verify(repository).save(pedidoExistente);
    }

    @Test
    @DisplayName("Debe lanzar NoDataFoundException si el pedido no existe al actualizar")
    void update_PedidoInexistente_LanzaNoDataFoundException() {
        Long idInexistente = 999L;
        PedidoDTO dto = PedidoDTO.builder().id(idInexistente).detalles(List.of()).build();

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(NoDataFoundException.class, () -> service.update(idInexistente, dto));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Debe eliminar el pedido si existe en base de datos")
    void delete_PedidoExistente_EliminaCorrectamente() {
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> service.delete(id));
        verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("Debe lanzar NoDataFoundException si el pedido a eliminar no existe")
    void delete_PedidoInexistente_LanzaExcepcion() {
        Long id = 999L;
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(NoDataFoundException.class, () -> service.delete(id));
        verify(repository, never()).deleteById(any());
    }
}
