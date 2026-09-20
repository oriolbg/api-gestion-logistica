package erp.gestion.apilogistica.repository;


import erp.gestion.apilogistica.entity.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
	
	Page<Pedido> findByFechaBetween(Pageable pageable, LocalDate fechaInicio, LocalDate fechaFin);

	Page<Pedido> findByCorrelativo(Pageable pageable, String nombre);
    
}
