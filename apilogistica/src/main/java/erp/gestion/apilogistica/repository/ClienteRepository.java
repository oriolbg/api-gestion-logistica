package erp.gestion.apilogistica.repository;


import erp.gestion.apilogistica.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ClienteRepository extends JpaRepository<Cliente, Long> {
	
	Cliente findByNumeroDocumento(String numeroDocumento);
	Page<Cliente> findByNombreContainingIgnoreCase(Pageable pageable, String nombre);
    
}
