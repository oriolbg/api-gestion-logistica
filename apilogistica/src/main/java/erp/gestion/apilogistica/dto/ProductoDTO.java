package erp.gestion.apilogistica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "La unidad es requerida")
    private String unidadId;
    private String unidadNombre;

    @NotBlank(message = "El codigo del producto es requerido")
    @Size(max = 50, message = "El código no debe exceder los 50 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre del producto es requerido")
    @Size(max = 70, message = "El nombre del producto no debe exceder los 70 caracteres")
    private String descripcion;

    @NotBlank(message = "El código de impuesto es requerido")
    private String codImp;

    @NotNull(message = "El precio base es requerido")
    @PositiveOrZero(message = "El precio base debe ser mayor o igual que cero")
    private BigDecimal precioBase;

    @NotNull(message = "El precio unitario es requerido")
    @PositiveOrZero(message = "El precio unitario debe ser mayor o igual que cero")
    private BigDecimal precioUnitario;
    private LocalDate fechaAlta;
    private boolean activo;
}