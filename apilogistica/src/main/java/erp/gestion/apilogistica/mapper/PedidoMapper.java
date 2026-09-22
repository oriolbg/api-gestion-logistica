package erp.gestion.apilogistica.mapper;

import erp.gestion.apilogistica.dto.PedidoDTO;
import erp.gestion.apilogistica.entity.Cliente;
import erp.gestion.apilogistica.entity.Pedido;
import erp.gestion.apilogistica.entity.TipoComprobante;
import org.mapstruct.*;

@Mapper(config = CentralMapperConfig.class, uses = {PedidoDetalleMapper.class})
public interface PedidoMapper extends GenericMapper<Pedido, PedidoDTO> {

    @Override
    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "clienteNombre", source = "cliente.nombre")
    @Mapping(target = "tipoComprobanteCodigo", source = "tipoComprobante.codigo")
    @Mapping(target = "tipoComprobanteDescripcion", source = "tipoComprobante.descripcion")
    @Mapping(target = "productoNombre", ignore = true)
    PedidoDTO toDTO(Pedido entity);

    @Override
    @Mapping(target = "cliente", source = "clienteId")                     // Utiliza mapClienteFromId
    @Mapping(target = "tipoComprobante", source = "tipoComprobanteCodigo") // Utiliza mapTipoComprobanteFromCodigo
    Pedido toEntity(PedidoDTO dto);

    @Override
    @InheritConfiguration(name = "toEntity")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fecha", ignore = true)
    void updateEntityFromDto(PedidoDTO dto, @MappingTarget Pedido entity);

    @AfterMapping
    default void vincularDetalles(@MappingTarget Pedido pedido) {
        if (pedido.getDetalles() != null) {
            pedido.getDetalles().forEach(detalle -> detalle.setPedido(pedido));
        }
    }

    // Métodos utilitarios que resuelven las entidades relacionadas sin ensuciar con @Mapping
    default Cliente mapClienteFromId(Long id) {
        if (id == null) return null;
        Cliente c = new Cliente();
        c.setId(id);
        return c;
    }

    default TipoComprobante mapTipoComprobanteFromCodigo(String codigo) {
        if (codigo == null) return null;
        TipoComprobante tc = new TipoComprobante();
        tc.setCodigo(codigo);
        return tc;
    }
}