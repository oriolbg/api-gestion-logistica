package erp.gestion.apilogistica.util;

import erp.gestion.apilogistica.dto.UsuarioDTO;
import erp.gestion.apilogistica.dto.UsuarioLoginDTO;
import org.springframework.stereotype.Component;

@Component
public class MapperUtil {

    public UsuarioLoginDTO secureCreateUserLogin(UsuarioDTO obj){
        return UsuarioLoginDTO.builder()
                .id(obj.getId())
                .email(obj.getEmail())
                .build();
    }
}
