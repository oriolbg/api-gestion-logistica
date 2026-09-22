package erp.gestion.apilogistica.mapper;

import erp.gestion.apilogistica.dto.ProductoDTO;
import erp.gestion.apilogistica.entity.Producto;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface ProductoMapper extends GenericMapper<Producto, ProductoDTO> {

    @Override
    @Mapping(target = "unidadId", source = "unidad.id")
    @Mapping(target = "unidadNombre", source = "unidad.descripcion")
    ProductoDTO toDTO(Producto entity);

    @Override
    @Mapping(target = "unidad.id", source = "unidadId")
    @Mapping(target = "unidad.descripcion", ignore = true)
    @Mapping(target = "precioBase", source = "precioUnitario") // Se corrige el campo omitido usando valor por defecto
    @Mapping(target = "observaciones", ignore = true)           // Explicitamos que es opcional/ignorado
    Producto toEntity(ProductoDTO dto);

    @Override
    @InheritConfiguration(name = "toEntity")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaAlta", ignore = true) // La fecha original de alta no se modifica en un update
    void updateEntityFromDto(ProductoDTO dto, @MappingTarget Producto entity);
}