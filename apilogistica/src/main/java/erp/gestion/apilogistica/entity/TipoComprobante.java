package erp.gestion.apilogistica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tipo_comprobante")
public class TipoComprobante {
	
	@Id
	@Column(length = 2)
    private String codigo;
    
    @Column(nullable = false)
    private String descripcion;
}
