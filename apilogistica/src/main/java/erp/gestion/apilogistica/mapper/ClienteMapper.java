package erp.gestion.apilogistica.mapper;


import erp.gestion.apilogistica.dto.ClienteDTO;
import erp.gestion.apilogistica.entity.Cliente;
import erp.gestion.apilogistica.entity.TipoDocumento;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper extends GenericMapper<Cliente, ClienteDTO>{

	@Override
	public ClienteDTO toDTO(Cliente entity) {
		 if (entity == null) {
	            return null;
	        }
	        return ClienteDTO.builder()
	                .id(entity.getId())
	                .nombre(entity.getNombre())
	                .descripcionDocumento(entity.getTipoDocumento().getDescripcion())
	                .numeroDocumento(entity.getNumeroDocumento())
	                .direccion(entity.getDireccion())
	                .telefono(entity.getTelefono())
	                .email(entity.getEmail())
	                .build();
	}

	@Override
	public Cliente toEntity(ClienteDTO dto) {
		 if(dto==null){
	            return null;
	        }
		 return Cliente.builder()
	                .id(dto.getId())
	                .nombre(dto.getNombre())
	                .tipoDocumento(dto.getDescripcionDocumento() != null ? TipoDocumento.valueOf(dto.getDescripcionDocumento()) : null)
	                .numeroDocumento(dto.getNumeroDocumento())
	                .direccion(dto.getDireccion())
	                .telefono(dto.getTelefono())
	                .email(dto.getEmail())
	                .build();
	}

}
