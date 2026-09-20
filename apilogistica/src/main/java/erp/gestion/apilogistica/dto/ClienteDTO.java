package erp.gestion.apilogistica.dto;

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
	private String nombre;
	private String descripcionDocumento;
	private String numeroDocumento;
	private String direccion;
	private String telefono;
	private String email;
}
