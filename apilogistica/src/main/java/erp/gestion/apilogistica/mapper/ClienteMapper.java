package erp.gestion.apilogistica.mapper;

import erp.gestion.apilogistica.dto.ClienteDTO;
import erp.gestion.apilogistica.entity.Cliente;
import erp.gestion.apilogistica.entity.TipoDocumento;
import org.mapstruct.*;

import java.util.Arrays;

@Mapper(config = CentralMapperConfig.class)
public interface ClienteMapper extends GenericMapper<Cliente, ClienteDTO> {

    @Override
    @Mapping(target = "descripcionDocumento", source = "tipoDocumento", qualifiedByName = "tipoDocumentoToString")
    ClienteDTO toDTO(Cliente entity);

    @Override
    @InheritInverseConfiguration
    @Mapping(target = "tipoDocumento", source = "descripcionDocumento", qualifiedByName = "stringToTipoDocumento")
    Cliente toEntity(ClienteDTO dto);

    @Override
    @InheritConfiguration(name = "toEntity")
    @Mapping(target = "id", ignore = true) // El ID de la entidad existente no debe sobreescribirse
    void updateEntityFromDto(ClienteDTO dto, @MappingTarget Cliente entity);

    // Mapeos controlados y seguros para el Enum:
    @Named("tipoDocumentoToString")
    default String tipoDocumentoToString(TipoDocumento tipo) {
        return tipo != null ? tipo.getDescripcion() : null;
    }

    @Named("stringToTipoDocumento")
    default TipoDocumento stringToTipoDocumento(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            return null;
        }
        return Arrays.stream(TipoDocumento.values())
                .filter(td -> td.name().equalsIgnoreCase(descripcion) || td.getDescripcion().equalsIgnoreCase(descripcion))
                .findFirst()
                .orElse(null);
    }
}