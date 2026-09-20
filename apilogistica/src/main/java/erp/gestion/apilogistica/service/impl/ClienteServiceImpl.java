package erp.gestion.apilogistica.service.impl;



import erp.gestion.apilogistica.dto.ClienteDTO;
import erp.gestion.apilogistica.entity.Cliente;
import erp.gestion.apilogistica.exception.NoDataFoundException;
import erp.gestion.apilogistica.mapper.ClienteMapper;
import erp.gestion.apilogistica.repository.ClienteRepository;
import erp.gestion.apilogistica.service.ClienteService;
import erp.gestion.apilogistica.validator.ClienteValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {
    
	private final ClienteRepository repository;
    private final ClienteMapper mapper;
    
    public ClienteServiceImpl(ClienteRepository repository, ClienteMapper mapper){
        this.repository=repository;
        this.mapper = mapper;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ClienteDTO> findAll(Pageable pageable, String search) {
        Page<Cliente> clientes = (search==null || search.trim().isEmpty())
				                ? repository.findAll(pageable)
				                : repository.findByNombreContainingIgnoreCase(pageable, search);
     
        return clientes.map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO findById(Long id) {
    	Cliente entidad = repository.findById(id).orElseThrow(() -> new NoDataFoundException("No existe un registro con ese ID"));
        return mapper.toDTO(entidad);
    }

    @Override
    public ClienteDTO create(ClienteDTO obj) {
    	ClienteValidator.save(obj);
    	Cliente entidad = mapper.toEntity(obj);
    	Cliente saved = repository.save(entidad);
        return mapper.toDTO(saved);
    }

    @Override
    public ClienteDTO update(Long id, ClienteDTO obj) {
        ClienteValidator.save(obj);
        // Buscar el cliente existente
        Cliente entidad = repository.findById(id).orElseThrow(() -> new NoDataFoundException("No existe un registro con ese ID"));
     
        // Convertir DTO a entidad temporal para obtener valores convertidos
        Cliente datosNuevos = mapper.toEntity(obj);
     
        // Copiar campos a la entidad existente
        entidad.setNombre(datosNuevos.getNombre());
        entidad.setTipoDocumento(datosNuevos.getTipoDocumento());
        entidad.setNumeroDocumento(datosNuevos.getNumeroDocumento());
        entidad.setDireccion(datosNuevos.getDireccion());
        entidad.setTelefono(datosNuevos.getTelefono());
        entidad.setEmail(datosNuevos.getEmail());
     
        Cliente saved = repository.save(entidad);
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
