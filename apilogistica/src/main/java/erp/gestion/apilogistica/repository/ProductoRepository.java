package erp.gestion.apilogistica.repository;


import erp.gestion.apilogistica.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
	
    boolean existsByCodigo(String codigo);
    Page<Producto> findByDescripcionContainingIgnoreCase(Pageable pageable, String descripcion);
    
}
