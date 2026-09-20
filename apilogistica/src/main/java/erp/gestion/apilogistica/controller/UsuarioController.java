package erp.gestion.apilogistica.controller;



import erp.gestion.apilogistica.dto.UsuarioDTO;
import erp.gestion.apilogistica.dto.UsuarioLoginDTO;
import erp.gestion.apilogistica.dto.WrapperResponse;
import erp.gestion.apilogistica.service.UsuarioService;
import erp.gestion.apilogistica.util.MapperUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/usuarios")
public class UsuarioController {
	
    private final UsuarioService service;

    private MapperUtil mapperUtil;

    public UsuarioController(UsuarioService service, MapperUtil mapperUtil){
        this.service=service;
        this.mapperUtil = mapperUtil;
    }

    @PreAuthorize("hasRole('ADMIN')")//Requiere rol de ADMIN
    @GetMapping
    public ResponseEntity<WrapperResponse<Page<UsuarioDTO>>> findAll(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(value = "limit", required = false, defaultValue = "5") int pageSize
    ){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<UsuarioDTO> page = service.findAll(pageable, search);
        return new WrapperResponse<>(page, true, "success").createResponse(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<WrapperResponse<UsuarioDTO>> findById(@PathVariable Integer id) {
    	UsuarioDTO dto = service.findById(id);
        return new WrapperResponse<>(dto, true, "success").createResponse(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<WrapperResponse<UsuarioLoginDTO>> create(@Valid @RequestBody UsuarioDTO obj){
        UsuarioDTO created = service.create(obj);
        return new WrapperResponse<>(mapperUtil.secureCreateUserLogin(created), true, "success").createResponse(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<WrapperResponse<UsuarioDTO>> update(@PathVariable Integer id, @Valid @RequestBody UsuarioDTO obj) {
    	UsuarioDTO edited = service.update(id, obj);
    	return new WrapperResponse<>(edited, true, "success").createResponse(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<WrapperResponse<Void>> delete(@PathVariable Integer id) {
        service.delete(id);
        return new WrapperResponse<Void>(null, true, "success").createResponse(HttpStatus.OK);
    }
}
