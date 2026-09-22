package erp.gestion.apilogistica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClienteDTO {

	private Long id;

	@NotBlank(message = "El nombre del cliente es requerido")
	@Size(max = 100, message = "El nombre no debe exceder los 100 caracteres")
	private String nombre;

	@NotBlank(message = "El tipo de documento es requerido")
	private String descripcionDocumento;

	@NotBlank(message = "El número de documento es requerido")
	@Size(max = 20, message = "El número de documento no debe exceder los 20 caracteres")
	private String numeroDocumento;

	@Size(max = 100, message = "La direccion del cliente no debe exceder los 100 20 caracteres")
	private String direccion;

	@Size(max = 15, message = "El número de teléfono no debe exceder los 15 caracteres")
	private String telefono;

	@Email(message = "El formato del email no es válido")
	private String email;
}
