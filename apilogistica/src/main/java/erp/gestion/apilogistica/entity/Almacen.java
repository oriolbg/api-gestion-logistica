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
@Table(name = "almacenes")
public class Almacen {

    @Id
    @Column(length = 4, nullable = false)
    private String codigo_almacen;

    @Column(length = 45, nullable = false)
    private String descripcion_almacen;
}
