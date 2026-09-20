package erp.gestion.apilogistica.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductoDTO {
    private Long id;
    private String unidadId;
    private String unidadNombre;
    private String codigo;
    private String descripcion;
    private String codImp;
    private BigDecimal precioUnitario;
    private LocalDate fechaAlta;
    private boolean activo;
}