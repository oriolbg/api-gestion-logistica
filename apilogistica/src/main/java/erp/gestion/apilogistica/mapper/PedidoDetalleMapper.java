package erp.gestion.apilogistica.mapper;

import erp.gestion.apilogistica.dto.PedidoDetalleDTO;
import erp.gestion.apilogistica.entity.PedidoDetalle;
import erp.gestion.apilogistica.entity.Producto;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface PedidoDetalleMapper extends GenericMapper<PedidoDetalle, PedidoDetalleDTO> {

    @Override
    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "productoNombre", source = "producto.descripcion")
    PedidoDetalleDTO toDTO(PedidoDetalle entity);

    @Override
    @Mapping(target = "producto", source = "productoId") // Utiliza el método default mapProductoFromId
    @Mapping(target = "pedido", ignore = true)           // El pedido se vincula en PedidoMapper mediante @AfterMapping
    PedidoDetalle toEntity(PedidoDetalleDTO dto);

    @Override
    @InheritConfiguration(name = "toEntity")            // Hereda las reglas de 'toEntity'
    @Mapping(target = "id", ignore = true)              // En actualización, no sobreescribir el ID existente
    @Mapping(target = "pedido", ignore = true)
    void updateEntityFromDto(PedidoDetalleDTO dto, @MappingTarget PedidoDetalle entity);

    // Método de soporte: MapStruct lo usará automáticamente para convertir Long -> Producto
    default Producto mapProductoFromId(Long productoId) {
        if (productoId == null) {
            return null;
        }
        Producto producto = new Producto();
        producto.setId(productoId);
        return producto;
    }
}