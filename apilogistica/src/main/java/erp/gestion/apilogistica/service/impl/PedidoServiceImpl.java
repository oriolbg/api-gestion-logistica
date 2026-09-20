package erp.gestion.apilogistica.service.impl;


import erp.gestion.apilogistica.dto.PedidoDTO;
import erp.gestion.apilogistica.entity.Pedido;
import erp.gestion.apilogistica.exception.NoDataFoundException;
import erp.gestion.apilogistica.mapper.PedidoMapper;
import erp.gestion.apilogistica.repository.PedidoRepository;
import erp.gestion.apilogistica.service.PedidoService;
import erp.gestion.apilogistica.validator.PedidoValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Transactional
public class PedidoServiceImpl implements PedidoService {
    
	private final PedidoRepository repository;
    private final PedidoMapper mapper;
    
    public PedidoServiceImpl(PedidoRepository repository, PedidoMapper mapper){
        this.repository=repository;
        this.mapper = mapper;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PedidoDTO> findAll(Pageable pageable, String search) {
        Page<Pedido> pedidos = (search==null || search.trim().isEmpty())
				            ? repository.findAll(pageable)
				            : repository.findByCorrelativo(pageable, search);
     
        return pedidos.map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoDTO findById(Long id) {
    	Pedido entidad = repository.findById(id).orElseThrow(() -> new NoDataFoundException("No existe un registro con ese ID"));
        return mapper.toDTO(entidad);
    }

    @Override
    public PedidoDTO create(PedidoDTO obj) {
    	PedidoValidator.save(obj);
    	Pedido entidad = mapper.toEntity(obj);
    	entidad.setFecha(LocalDate.now());
    	BigDecimal total = obj.getDetalles().stream()
    			.map(item -> item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad())))
    			.reduce(BigDecimal.ZERO, BigDecimal::add);
    	entidad.setTotal(total);
    	Pedido saved = repository.save(entidad);
        return mapper.toDTO(saved);
    }

    @Override
    public PedidoDTO update(Long id, PedidoDTO obj) {
        PedidoValidator.save(obj);
     
        // Buscar el pedido existente y guardar la fecha original
        Pedido pedidoExistente = repository.findById(id).orElseThrow(() -> new NoDataFoundException("No existe un registro con ese ID"));
     
        LocalDate fechaOriginal = pedidoExistente.getFecha();  // Guardar fecha original
     
        // Convertir DTO a entidad
        Pedido entidad = mapper.toEntity(obj);
        entidad.setId(id);
        entidad.setFecha(fechaOriginal);  //Restaurar fecha original
     
        // Calcular total
        BigDecimal total = obj.getDetalles().stream()
                .map(item -> item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        entidad.setTotal(total);
     
        Pedido saved = repository.save(entidad);
        return mapper.toDTO(saved);
    }

    @Override
    public void delete(Long id) {
        if(!repository.existsById(id)){
            throw new NoDataFoundException("No existe un registro con ese ID.");
        }
        repository.deleteById(id);
    }
}
