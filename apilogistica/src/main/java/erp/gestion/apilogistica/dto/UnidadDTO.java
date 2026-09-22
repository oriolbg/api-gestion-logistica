package erp.gestion.apilogistica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UnidadDTO {
    private String id;

    @NotBlank(message = "La descripcion de la unidad es requerido")
    @Size(max = 50, message = "La descripcion de la unidad no debe exceder los 50 caracteres")
    private String descripcion;
}