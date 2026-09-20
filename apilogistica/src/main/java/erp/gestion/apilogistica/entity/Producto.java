package erp.gestion.apilogistica.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    
    @Column(name = "unidad_id", length = 3, nullable = false)
    private String unidadId;
    
    @Column(length = 50, nullable = true)
    private String codigo;
    
    @Column(length = 70, nullable = false)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Unidad unidad;

    @Column(name = "codimp", nullable = false)
    private String codImp;

    @Column(name = "precio_unitario", precision = 8, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    private LocalDate fechaAlta;

    @Column(nullable = false)
    private boolean activo;

    private String observaciones;
}
