package erp.gestion.apilogistica.service.impl;



import erp.gestion.apilogistica.dto.ProductoDTO;
import erp.gestion.apilogistica.entity.Producto;
import erp.gestion.apilogistica.entity.Unidad;
import erp.gestion.apilogistica.exception.NoDataFoundException;
import erp.gestion.apilogistica.exception.ValidateException;
import erp.gestion.apilogistica.mapper.ProductoMapper;
import erp.gestion.apilogistica.repository.ProductoRepository;
import erp.gestion.apilogistica.repository.UnidadRepository;
import erp.gestion.apilogistica.service.ProductoService;
import erp.gestion.apilogistica.validator.ProductoValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {
    
    private final ProductoRepository repository;
    private final UnidadRepository unidadRepository;
    private final ProductoMapper mapper;
    
    public ProductoServiceImpl(ProductoRepository repository, UnidadRepository unidadRepository, ProductoMapper mapper){
        this.repository = repository;
        this.unidadRepository = unidadRepository;
        this.mapper = mapper;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDTO> findAll(Pageable pageable, String search) {
        Page<Producto> productos = (search==null || search.trim().isEmpty()) 
					        		? repository.findAll(pageable)
					        		: repository.findByDescripcionContainingIgnoreCase(pageable, search);
        		
    	return productos.map(mapper::toDTO);
    	
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDTO findById(Long id) {
        Producto entidad = repository.findById(id).orElseThrow(() -> new NoDataFoundException("No existe un registro con ese ID"));
        return mapper.toDTO(entidad);
    }

    @Override
    public ProductoDTO create(ProductoDTO obj) {
    	ProductoValidator.save(obj);
    	Unidad unidad = unidadRepository.findById(obj.getUnidadId())
    			.orElseThrow(() -> new ValidateException("La unidad especificada no existe"));
    	Producto entidad = mapper.toEntity(obj);
    	entidad.setUnidad(unidad);
    	Producto saved = repository.save(entidad);
        return mapper.toDTO(saved);
    }

    @Override
    public ProductoDTO update(Long id, ProductoDTO obj) {
    	ProductoValidator.save(obj);
    	Producto entidad = repository.findById(id).orElseThrow(() -> new NoDataFoundException("No existe un registro con ese ID"));
    	Unidad unidad = unidadRepository.findById(obj.getUnidadId())
    			.orElseThrow(() -> new ValidateException("La unidad especificada no existe"));
    	
    	entidad.setCodigo(obj.getCodigo());
    	entidad.setDescripcion(obj.getDescripcion());
    	entidad.setPrecioUnitario(obj.getPrecioUnitario());
    	entidad.setUnidad(unidad);

        Producto saved = repository.save(entidad);
        return mapper.toDTO(saved);

    }

    @Override
    public void delete(Long id) {
    	if(!repository.existsById(id)) {
    		throw new NoDataFoundException("No existe un registro con ese ID");
    	}
        repository.deleteById(id);
    }
}
