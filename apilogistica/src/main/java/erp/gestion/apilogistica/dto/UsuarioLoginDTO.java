package erp.gestion.apilogistica.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioLoginDTO {

    public Integer id;
    public String email;
    public String password;
}
