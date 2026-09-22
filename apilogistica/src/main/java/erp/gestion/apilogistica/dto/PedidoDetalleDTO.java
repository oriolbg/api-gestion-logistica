package erp.gestion.apilogistica.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidoDetalleDTO {
    private Long id;

    @NotNull(message = "El codigo de producto es requerido")
    private Long productoId;
    private String productoNombre;

    @NotNull(message = "La cantidad es requerida")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es requerido")
    @Positive(message = "El precio unitario debe ser mayor a cero")
    private BigDecimal precioUnitario;

    @NotNull(message = "El subtotal es requerido")
    @Positive(message = "El subtotal debe ser mayor a cero en")
    private BigDecimal subtotal;
}