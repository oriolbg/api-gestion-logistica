package erp.gestion.apilogistica.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pedidos")
public class Pedido {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private LocalDate fecha;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cliente_id", nullable = false)
	private Cliente cliente;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tipo_comprobante_codigo", nullable = false)
	private TipoComprobante tipoComprobante;

	@Column(length = 4)
	private String serie;
	
	private int correlativo;
	
	@Column(precision = 8, scale = 2)
	private BigDecimal total;
	
	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PedidoDetalle> detalles = new ArrayList<>();
}
