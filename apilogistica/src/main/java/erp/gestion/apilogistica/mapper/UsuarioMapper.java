package erp.gestion.apilogistica.mapper;


import erp.gestion.apilogistica.dto.RolDTO;
import erp.gestion.apilogistica.dto.UsuarioDTO;
import erp.gestion.apilogistica.entity.Rol;
import erp.gestion.apilogistica.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper extends GenericMapper<Usuario, UsuarioDTO>{

	@Override
	public UsuarioDTO toDTO(Usuario entity) {
		 if (entity == null) {
	            return null;
	        }
	     
		 List<RolDTO> rolesDto = entity.getRoles().stream()
				 .map(rol -> new RolDTO(rol.getId(), rol.getNombre()))
				 .collect(Collectors.toList());
		 
		 return UsuarioDTO.builder()
				 .id(entity.getId())
				 .email(entity.getEmail())
				 .activo(entity.isActivo())
				 .roles(rolesDto)
				 .build();
	}

	@Override
	public Usuario toEntity(UsuarioDTO dto) {
		 if(dto==null){
	            return null;
	        }
		 
		 Set<Rol> roles = dto.getRoles()!=null ? dto.getRoles().stream()
				.map(rolDto -> Rol.builder()
						.id(rolDto.getId())
						.nombre(rolDto.getNombre())
						.build()
				)
				.collect(Collectors.toSet())
				: new HashSet<>();
		 
		 return Usuario.builder()
				 .id(dto.getId())
				 .email(dto.getEmail())
				 .password(dto.getPassword())
				 .activo(dto.isActivo())
				 .roles(roles)
				 .build();
	}

}
