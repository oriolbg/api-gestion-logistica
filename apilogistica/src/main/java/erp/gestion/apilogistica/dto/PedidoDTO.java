package erp.gestion.apilogistica.dto;

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
    private Long clienteId;
    private String clienteNombre;
    private String productoNombre;
    private String tipoComprobanteCodigo;
    private String tipoComprobanteDescripcion;
    private String serie;
    private int correlativo;
    private BigDecimal total;
    private List<PedidoDetalleDTO> detalles;
}