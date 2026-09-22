package erp.gestion.apilogistica.service.impl;



import erp.gestion.apilogistica.dto.UsuarioDTO;
import erp.gestion.apilogistica.entity.Usuario;
import erp.gestion.apilogistica.exception.NoDataFoundException;
import erp.gestion.apilogistica.exception.ValidateException;
import erp.gestion.apilogistica.mapper.UsuarioMapper;
import erp.gestion.apilogistica.repository.UsuarioRepository;
import erp.gestion.apilogistica.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {
    
	private final UsuarioRepository repository;
    private final UsuarioMapper mapper;
    private final PasswordEncoder passwordEncoder;
    
    public UsuarioServiceImpl(UsuarioRepository repository, UsuarioMapper mapper, PasswordEncoder passwordEncoder){
        this.repository=repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioDTO> findAll(Pageable pageable, String search) {
        Page<Usuario> usuarios = (search==null || search.trim().isEmpty())
				                ? repository.findAll(pageable)
				                : repository.findByEmailContainingIgnoreCase(pageable, search);
        
        return usuarios.map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO findById(Integer id) {
    	Usuario entidad = repository.findById(id).orElseThrow(() -> new NoDataFoundException("No existe un registro con ese ID"));
        return mapper.toDTO(entidad);
    }

    @Override
    public UsuarioDTO create(UsuarioDTO obj) {
    	if(repository.findByEmail(obj.getEmail()).isPresent()) {
    		throw new ValidateException("El email ya está registrado");
    	}
    	Usuario usuario = mapper.toEntity(obj);
    	usuario.setPassword(passwordEncoder.encode(obj.getPassword()));
    	Usuario saved = repository.save(usuario);
        return mapper.toDTO(saved);
    }

    @Override
    public UsuarioDTO update(Integer id, UsuarioDTO obj) {
     
        Usuario entidad = repository.findById(id).orElseThrow(() -> new NoDataFoundException("No existe un registro con ese ID"));
     
        Usuario datosNuevos = mapper.toEntity(obj);
     
        // Actualizar campos directamente en la entidad existente
        entidad.setEmail(datosNuevos.getEmail());
        entidad.setActivo(datosNuevos.isActivo());
        entidad.setRoles(datosNuevos.getRoles());
     
        if (obj.getPassword() != null && !obj.getPassword().isEmpty()) {
            entidad.setPassword(passwordEncoder.encode(obj.getPassword()));
        }
     
        Usuario saved = repository.save(entidad);
        return mapper.toDTO(saved);
    }

    @Override
    public void delete(Integer id) {
        if(!repository.existsById(id)){
            throw new NoDataFoundException("No existe un registro con ese ID.");
        }
        repository.deleteById(id);
    }
}
