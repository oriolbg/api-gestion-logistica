package erp.gestion.apilogistica.repository;


import erp.gestion.apilogistica.entity.TipoEstadoGestion;
import erp.gestion.apilogistica.entity.TipoEstadoGestionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoEstadoGestionRepository extends JpaRepository<TipoEstadoGestion, TipoEstadoGestionId> {
}
