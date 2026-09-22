package erp.gestion.apilogistica.repository;


import erp.gestion.apilogistica.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
	
    boolean existsByCodigo(String codigo);

    @Override
    @EntityGraph(attributePaths = {"unidad"})
    Page<Producto> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"unidad"})
    Page<Producto> findByDescripcionContainingIgnoreCase(Pageable pageable, String descripcion);
    
    @Override
    @EntityGraph(attributePaths = {"unidad"})
    Optional<Producto> findById(Long id);
}
