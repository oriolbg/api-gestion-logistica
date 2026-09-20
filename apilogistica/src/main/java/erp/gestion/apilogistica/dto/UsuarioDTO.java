package erp.gestion.apilogistica.dto;

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
	private String email;
	private String password;
	private Boolean activo;
	private List<RolDTO> roles;
	
	public boolean isActivo() {
		return activo;
	}
}
