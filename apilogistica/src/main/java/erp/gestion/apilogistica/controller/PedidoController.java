package erp.gestion.apilogistica.controller;



import erp.gestion.apilogistica.dto.PedidoDTO;
import erp.gestion.apilogistica.dto.WrapperResponse;
import erp.gestion.apilogistica.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasAnyRole('ADMIN')")
@RequestMapping("api/pedidos")
public class PedidoController {
	
    private final PedidoService service;

    public PedidoController(PedidoService service){
        this.service=service;
    }

    @GetMapping
    public ResponseEntity<WrapperResponse<Page<PedidoDTO>>> findAll(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(value = "limit", required = false, defaultValue = "5") int pageSize
    ){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<PedidoDTO> page = service.findAll(pageable, search);
        return new WrapperResponse<>(page, true, "success").createResponse(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WrapperResponse<PedidoDTO>> findById(@PathVariable Long id) {
    	PedidoDTO dto = service.findById(id);
        return new WrapperResponse<>(dto, true, "success").createResponse(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<WrapperResponse<PedidoDTO>> create(@Valid @RequestBody PedidoDTO obj){
    	PedidoDTO created = service.create(obj);
        return new WrapperResponse<>(created, true, "success").createResponse(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WrapperResponse<PedidoDTO>> update(@PathVariable Long id, @Valid @RequestBody PedidoDTO obj) {
    	PedidoDTO edited = service.update(id, obj);
    	return new WrapperResponse<>(edited, true, "success").createResponse(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WrapperResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return new WrapperResponse<Void>(null, true, "success").createResponse(HttpStatus.OK);
    }
}
