package erp.gestion.apilogistica.mapper;

import erp.gestion.apilogistica.dto.UnidadDTO;
import erp.gestion.apilogistica.entity.Unidad;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface UnidadMapper extends GenericMapper<Unidad, UnidadDTO> {

    @Override
    @InheritConfiguration(name = "toEntity")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UnidadDTO dto, @MappingTarget Unidad entity);
}