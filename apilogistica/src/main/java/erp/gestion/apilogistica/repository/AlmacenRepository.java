package erp.gestion.apilogistica.repository;


import erp.gestion.apilogistica.entity.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlmacenRepository extends JpaRepository<Almacen, String> {
}
