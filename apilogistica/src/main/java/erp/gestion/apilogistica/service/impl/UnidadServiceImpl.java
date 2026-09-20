package erp.gestion.apilogistica.service.impl;



import erp.gestion.apilogistica.entity.Unidad;
import erp.gestion.apilogistica.exception.NoDataFoundException;
import erp.gestion.apilogistica.repository.UnidadRepository;
import erp.gestion.apilogistica.service.UnidadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UnidadServiceImpl implements UnidadService {
    
	private final UnidadRepository repository;

    public UnidadServiceImpl(UnidadRepository repository){
        this.repository=repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Unidad> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Unidad findById(String id) {
        return repository.findById(id).orElseThrow();
    }

    @Override
    public Unidad create(Unidad obj) {
        return repository.save(obj);
    }

    @Override
    public Unidad update(String id, Unidad obj) {
        Unidad entidad = repository.findById(id).orElseThrow(() -> new NoDataFoundException("No existe un registro con ese ID"));
        entidad.setDescripcion(obj.getDescripcion());
        return repository.save(entidad);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}
