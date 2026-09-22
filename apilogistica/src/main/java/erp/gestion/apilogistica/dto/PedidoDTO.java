package erp.gestion.apilogistica.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidoDTO {
    private Long id;
    private LocalDate fecha;

    @NotBlank(message = "El cliente es requerido")
    private Long clienteId;
    private String clienteNombre;
    private String productoNombre;

    @NotBlank(message = "El tipo de comprobante es requerido")
    private String tipoComprobanteCodigo;
    private String tipoComprobanteDescripcion;

    @NotBlank(message = "La serie es requerida")
    private String serie;

    @Positive(message = "El correlativo debe ser mayor a cero")
    private int correlativo;
    private BigDecimal total;

    @NotEmpty(message = "El pedido debe contener al menos un detalle")
    @Valid // <-- ¡Crucial!: Activa la validación en cascada para cada elemento de la lista
    private List<PedidoDetalleDTO> detalles;
}