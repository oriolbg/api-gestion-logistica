package erp.gestion.apilogistica.mapper;

import erp.gestion.apilogistica.dto.PedidoDTO;
import erp.gestion.apilogistica.entity.Pedido;
import org.mapstruct.*;

@Mapper(config = CentralMapperConfig.class, uses = {PedidoDetalleMapper.class})
public interface PedidoMapper extends GenericMapper<Pedido, PedidoDTO> {

    @Override
    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "clienteNombre", source = "cliente.nombre")
    @Mapping(target = "tipoComprobanteCodigo", source = "tipoComprobante.codigo")
    @Mapping(target = "tipoComprobanteDescripcion", source = "tipoComprobante.descripcion")
    @Mapping(target = "productoNombre", ignore = true) // Campo sobrante detectado en PedidoDTO
    PedidoDTO toDTO(Pedido entity);

    @Override
    @Mapping(target = "cliente.id", source = "clienteId")
    @Mapping(target = "cliente.nombre", ignore = true)
    @Mapping(target = "cliente.tipoDocumento", ignore = true)
    @Mapping(target = "cliente.numeroDocumento", ignore = true)
    @Mapping(target = "cliente.direccion", ignore = true)
    @Mapping(target = "cliente.telefono", ignore = true)
    @Mapping(target = "cliente.email", ignore = true)
    @Mapping(target = "tipoComprobante.codigo", source = "tipoComprobanteCodigo")
    @Mapping(target = "tipoComprobante.descripcion", ignore = true)
    Pedido toEntity(PedidoDTO dto);

    @Override
    @InheritConfiguration(name = "toEntity")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fecha", ignore = true)
    void updateEntityFromDto(PedidoDTO dto, @MappingTarget Pedido entity);

    // Mantiene la consistencia bidireccional JPA sin código duplicado en el Service
    @AfterMapping
    default void vincularDetalles(@MappingTarget Pedido pedido) {
        if (pedido.getDetalles() != null) {
            pedido.getDetalles().forEach(detalle -> detalle.setPedido(pedido));
        }
    }
}