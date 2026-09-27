package erp.gestion.apilogistica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class TipoEstadoGestionId implements Serializable {

    @Column(name = "id_gestion", nullable = false)
    private Integer idGestion;

    @Column(name = "id_estado", nullable = false)
    private Integer idEstado;

    @Column(name = "id_subestado", nullable = false)
    private Integer idSubestado;
}