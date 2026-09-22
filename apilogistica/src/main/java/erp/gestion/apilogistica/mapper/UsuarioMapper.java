package erp.gestion.apilogistica.mapper;

import erp.gestion.apilogistica.dto.RolDTO;
import erp.gestion.apilogistica.dto.UsuarioDTO;
import erp.gestion.apilogistica.entity.Rol;
import erp.gestion.apilogistica.entity.Usuario;
import org.mapstruct.*;

@Mapper(
        config = CentralMapperConfig.class,
        builder = @Builder(disableBuilder = true) // Desactiva el builder para tratar la entidad de forma homogénea
)
public interface UsuarioMapper extends GenericMapper<Usuario, UsuarioDTO> {

    @Override
    @Mapping(target = "password", ignore = true) // Seguridad: no exponer el hash al DTO
    UsuarioDTO toDTO(Usuario entity);

    @Override
    @Mapping(target = "authorities", ignore = true) // Ignora la colección calculada de UserDetails
    Usuario toEntity(UsuarioDTO dto);

    @Override
    @InheritConfiguration(name = "toEntity") // Hereda automáticamente el ignore de "authorities"
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // La contraseña se cifra en el Service
    void updateEntityFromDto(UsuarioDTO dto, @MappingTarget Usuario entity);

    // Mapeos auxiliares de colección Set<Rol> <-> List<RolDTO>
    RolDTO toRolDto(Rol rol);
    Rol toRolEntity(RolDTO rolDto);
}