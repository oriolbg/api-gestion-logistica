package erp.gestion.apilogistica.controller;



import erp.gestion.apilogistica.dto.ProductoDTO;
import erp.gestion.apilogistica.dto.WrapperResponse;
import erp.gestion.apilogistica.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@RequestMapping("api/productos")
@Tag(name = "Productos", description = "Api para gestionar los productos")
public class ProductoController {
	
    private final ProductoService service;

    public ProductoController(ProductoService service){
        this.service=service;
    }

    @GetMapping
    @Operation(
    		summary = "Obtener todos los productos",
    		description = "Devuelve una lista paginada de productos. Permite filtrar por nombre u otros campos"
    )
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200", description = "Lista de productos obtenida correctamente"),
    		@ApiResponse(responseCode = "500", description = "Error interno del servidor")
    	}
    )
    public ResponseEntity<WrapperResponse<Page<ProductoDTO>>> findAll(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(value = "limit", required = false, defaultValue = "5") int pageSize
    ){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<ProductoDTO> page = service.findAll(pageable, search);
        return new WrapperResponse<>(page, true, "success").createResponse(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(
    		summary = "Obtener un producto por ID",
    		description = "Devuelve los datos de un producto especifico"
    )
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200", description = "Producto encontrado"),
    		@ApiResponse(responseCode = "404", description = "Producto no encontrado")
    	}
    )
    public ResponseEntity<WrapperResponse<ProductoDTO>> findById(@PathVariable Long id) {
        ProductoDTO dto = service.findById(id);
        return new WrapperResponse<>(dto, true, "success").createResponse(HttpStatus.OK);
    }

    @PostMapping
    @Operation(
    		summary = "Crear un nuevo producto",
    		description = "Crea un producto con los datos enviados en el body"
    )
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "201", description = "Producto creado correctamente"),
    		@ApiResponse(responseCode = "400", description = "Solicitud invalida (datos incorrectos)")
    	}
    )
    public ResponseEntity<WrapperResponse<ProductoDTO>> create(@Valid @RequestBody ProductoDTO obj){
        ProductoDTO created = service.create(obj);
        return new WrapperResponse<>(created, true, "success").createResponse(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
    		summary = "Actualizar un producto",
    		description = "Actualiza un producto con los datos enviados en el body"
    )
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
    		@ApiResponse(responseCode = "400", description = "Solicitud invalida (datos incorrectos)"),
    		@ApiResponse(responseCode = "404", description = "Producto no encontrado")
    	}
    )
    public ResponseEntity<WrapperResponse<ProductoDTO>> update(@PathVariable Long id, @Valid @RequestBody ProductoDTO obj) {
    	ProductoDTO edited = service.update(id, obj);
    	return new WrapperResponse<>(edited, true, "success").createResponse(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(
    		summary = "Eliminar un producto",
    		description = "Eliminar un produto por su ID"
    )
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200", description = "Producto eliminado correctamente"),
    		@ApiResponse(responseCode = "404", description = "Producto no encontrado")
    	}
    )
    public ResponseEntity<WrapperResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return new WrapperResponse<Void>(null, true, "success").createResponse(HttpStatus.OK);
    }
}
