package erp.gestion.apilogistica.validator;


import erp.gestion.apilogistica.dto.PedidoDTO;
import erp.gestion.apilogistica.dto.PedidoDetalleDTO;
import erp.gestion.apilogistica.exception.ValidateException;

import java.math.BigDecimal;

public class PedidoValidator {
	 public static void save(PedidoDTO pedido) { 
		 if(pedido == null) {
			 throw new ValidateException("El pedido no puede ser nulo");
		 }
		 
		 if(pedido.getClienteId() == null) {
			 throw new ValidateException("El cliente es requerido");
		 }
		 
		 if(pedido.getTipoComprobanteCodigo() == null || pedido.getTipoComprobanteCodigo().trim().isEmpty()) {
			 throw new ValidateException("El tipo comprobante es requerido");
		 }
		 
		 if(pedido.getSerie() == null || pedido.getSerie().trim().isEmpty()) {
			 throw new ValidateException("La serie es requerida");
		 }
		 
		 if(pedido.getCorrelativo()<=0) {
			 throw new ValidateException("El correlativo es requerido");
		 }
		 /*
		 if(pedido.getTotal() == null || pedido.getTotal().compareTo(BigDecimal.ZERO)<=0) {
			 throw new ValidateException("El total del pedido debe ser mayor a cero");
		 }
		 */
		 if(pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
			 throw new ValidateException("El pedido debe tener al menos un detalle");
		 }
		 
		 for (PedidoDetalleDTO detalles : pedido.getDetalles()) {
			validateDetalle(detalles);
		}
    }
	
	private static void validateDetalle(PedidoDetalleDTO pedidoDetalle) {
		 if(pedidoDetalle.getProductoId() == null) {
			 throw new ValidateException("El producto es requerido en cada detalle");
		 }
		 
		 if(pedidoDetalle.getCantidad() == null || pedidoDetalle.getCantidad() <= 0) {
			 throw new ValidateException("La cantidad debe ser mayor a cero en cada detalle");
		 }
		 
		 if(pedidoDetalle.getPrecioUnitario() == null || pedidoDetalle.getPrecioUnitario().compareTo(BigDecimal.ZERO)<=0) {
			 throw new ValidateException("El precio unitario debe ser mayor a cero en cada detalle");
		 }
		 
		 if(pedidoDetalle.getSubtotal() == null || pedidoDetalle.getSubtotal().compareTo(BigDecimal.ZERO)<=0) {
			 throw new ValidateException("El subtotal debe ser mayor a cero en cada detalle");
		 }
		 
		 //Validar coherencia --> precioUnitario * cantidad = subtotal
		 BigDecimal subtotalEsperado = pedidoDetalle.getPrecioUnitario().multiply(new BigDecimal(pedidoDetalle.getCantidad()));
		 if(pedidoDetalle.getSubtotal().compareTo(subtotalEsperado) != 0) {
			 throw new ValidateException("LEl subtotla del detalle no coincide con cantidad * precio unitario"); 
		 }
	}
}
