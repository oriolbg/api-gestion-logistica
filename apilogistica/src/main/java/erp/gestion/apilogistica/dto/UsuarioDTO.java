package erp.gestion.apilogistica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {

	private Integer id;

	@NotBlank(message = "El email del usuario es requerido")
	@Size(max = 50, message = "El email del usuario no debe exceder los 70 caracteres")
	private String email;

	@NotBlank(message = "El password del usuario es requerido")
	@Size(max = 150, message = "El password no debe exceder los 150 caracteres")
	private String password;
	private Boolean activo;
	private List<RolDTO> roles;
	
	public boolean isActivo() {
		return activo;
	}
}
