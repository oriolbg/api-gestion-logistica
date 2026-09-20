package erp.gestion.apilogistica.repository;


import erp.gestion.apilogistica.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
	
    Page<Usuario> findByEmailContainingIgnoreCase(Pageable pageable, String email);

	Optional<Usuario> findByEmail(String email);
    
}
