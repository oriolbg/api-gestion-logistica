package erp.gestion.apilogistica.mapper;


import erp.gestion.apilogistica.dto.PedidoDTO;
import erp.gestion.apilogistica.entity.Cliente;
import erp.gestion.apilogistica.entity.Pedido;
import erp.gestion.apilogistica.entity.TipoComprobante;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PedidoMapper extends GenericMapper<Pedido, PedidoDTO>{
	
	private final PedidoDetalleMapper detalleMapper = new PedidoDetalleMapper();

	@Override
	public PedidoDTO toDTO(Pedido entity) {
		 if (entity == null) {
	            return null;
	        }
	        return PedidoDTO.builder()
	                .id(entity.getId())
	                .fecha(entity.getFecha())
	                .clienteId(entity.getCliente() != null ? entity.getCliente().getId() : null)
	                .clienteNombre(entity.getCliente() != null ? entity.getCliente().getNombre() : null)
	                .tipoComprobanteCodigo(entity.getTipoComprobante() != null ? entity.getTipoComprobante().getCodigo() : null)
	                .tipoComprobanteDescripcion(entity.getTipoComprobante() != null ? entity.getTipoComprobante().getDescripcion() : null)
	                .serie(entity.getSerie())
	                .correlativo(entity.getCorrelativo())
	                .total(entity.getTotal())
	                .detalles(entity.getDetalles() != null ? entity.getDetalles().stream()
	                		.map(detalleMapper::toDTO)
	                		.collect(Collectors.toList())
	                		: null)
	                .build();
	}

	@Override
	public Pedido toEntity(PedidoDTO dto) {
		 if(dto==null){
	            return null;
	        }
		 Pedido pedido = Pedido.builder()
	                .id(dto.getId())
	                .serie(dto.getSerie())
	                .correlativo(dto.getCorrelativo())
	                .total(dto.getTotal())
	                .build();
		 
		 if(dto.getClienteId() != null) {
			 Cliente cliente = new Cliente();
			 cliente.setId(dto.getClienteId());
			 pedido.setCliente(cliente);
		 }
		 
		 if(dto.getTipoComprobanteCodigo() != null) {
			 TipoComprobante tipo = new TipoComprobante();
			 tipo.setCodigo(dto.getTipoComprobanteCodigo());
			 pedido.setTipoComprobante(tipo);
		 }
		 
		 if(dto.getDetalles() != null) {
			 pedido.setDetalles(dto.getDetalles().stream()
					 .map(detalleMapper::toEntity)
					 .collect(Collectors.toList()));
			 
			 pedido.getDetalles().forEach(d -> d.setPedido(pedido));
		 }
		 
		 return pedido;
	}

}
