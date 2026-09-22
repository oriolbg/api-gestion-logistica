package erp.gestion.apilogistica.validator;


import erp.gestion.apilogistica.dto.PedidoDTO;
import erp.gestion.apilogistica.dto.PedidoDetalleDTO;
import erp.gestion.apilogistica.exception.ValidateException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PedidoValidator {

	//Control de importes del subtotal esperado y el calculado
	public void validarSubtotalesDetalle(PedidoDTO pedido) {
		for (PedidoDetalleDTO detalle : pedido.getDetalles()) {
			BigDecimal subtotalEsperado = detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
			if (detalle.getSubtotal().compareTo(subtotalEsperado) != 0) {
				throw new ValidateException("El subtotal del detalle no coincide con cantidad * precio unitario");
			}
		}
	}
}
