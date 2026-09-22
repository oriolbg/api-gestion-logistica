package erp.gestion.apilogistica.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "productos")
public class Producto {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 50, nullable = true)
    private String codigo;
    
    @Column(length = 70, nullable = false)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidad_id", nullable = false)
    private Unidad unidad;

    @Column(name = "codimp", nullable = false)
    private String codImp;

    @Column(name = "precio_base", precision = 8, scale = 2, nullable = false)
    private BigDecimal precioBase;

    @Column(name = "precio_unitario", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    private LocalDate fechaAlta;

    @Column(nullable = false)
    private boolean activo;

    private String observaciones;
}
