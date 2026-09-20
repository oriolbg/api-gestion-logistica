package erp.gestion.apilogistica.repository;


import erp.gestion.apilogistica.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {

	Optional<Rol> findByNombre(String nombre);
}
