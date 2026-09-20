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
@Table(name="unidades")
public class Unidad {
	
    @Id
    @Column(length = 3)
    private String id;

    @Column(length = 50, nullable = false)
    private String descripcion;
}
