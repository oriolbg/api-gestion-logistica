package erp.gestion.apilogistica.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tipo_estados_gestion")
public class TipoEstadoGestion {

    @EmbeddedId //Generamos una clase para las PK de la entidad y la usamos embedida
    private TipoEstadoGestionId id;

    @Column(length = 50, nullable = false)
    private String descripcion_estado;

    @Column(length = 50, nullable = false)
    private String descripcion_subestado;
}
