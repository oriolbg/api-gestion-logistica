package erp.gestion.apilogistica.validator;

import erp.gestion.apilogistica.dto.PedidoDTO;
import erp.gestion.apilogistica.dto.PedidoDetalleDTO;
import erp.gestion.apilogistica.exception.ValidateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests Unitarios - PedidoValidator")
class PedidoValidatorTest {

    private PedidoValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PedidoValidator();
    }

    @Test
    @DisplayName("Debe validar exitosamente cuando todos los subtotales coinciden con cantidad * precio")
    void validarSubtotalesDetalle_Exitoso() {
        PedidoDetalleDTO item1 = PedidoDetalleDTO.builder()
                .cantidad(2)
                .precioUnitario(new BigDecimal("50.00"))
                .subtotal(new BigDecimal("100.00"))
                .build();

        PedidoDetalleDTO item2 = PedidoDetalleDTO.builder()
                .cantidad(3)
                .precioUnitario(new BigDecimal("25.50"))
                .subtotal(new BigDecimal("76.50"))
                .build();

        PedidoDTO pedido = PedidoDTO.builder()
                .detalles(List.of(item1, item2))
                .build();

        assertDoesNotThrow(() -> validator.validarSubtotalesDetalle(pedido));
    }

    @Test
    @DisplayName("Debe lanzar ValidateException cuando el subtotal no coincide con cantidad * precio")
    void validarSubtotalesDetalle_SubtotalInvalido_LanzaExcepcion() {
        PedidoDetalleDTO itemErroneo = PedidoDetalleDTO.builder()
                .cantidad(2)
                .precioUnitario(new BigDecimal("50.00"))
                .subtotal(new BigDecimal("99.99")) // Esperado: 100.00
                .build();

        PedidoDTO pedido = PedidoDTO.builder()
                .detalles(List.of(itemErroneo))
                .build();

        ValidateException exception = assertThrows(
                ValidateException.class,
                () -> validator.validarSubtotalesDetalle(pedido)
        );

        assertEquals("El subtotal del detalle no coincide con cantidad * precio unitario", exception.getMessage());
    }
}
