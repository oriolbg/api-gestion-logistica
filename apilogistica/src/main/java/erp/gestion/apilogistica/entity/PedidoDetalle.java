package erp.gestion.apilogistica.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pedido_detalles")
public class PedidoDetalle {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id", nullable = false)
	private Pedido pedido;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "producto_id", nullable = false)
	private Producto producto;
	
	private int cantidad;
	
	@Column(name = "precio_unitario", precision = 8, scale = 2, nullable = false)
	private BigDecimal precioUnitario;
	
	@Column(precision = 8, scale = 2, nullable = false)
	private BigDecimal subtotal;
}
