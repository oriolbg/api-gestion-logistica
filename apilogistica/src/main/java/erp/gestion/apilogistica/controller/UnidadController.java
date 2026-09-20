package erp.gestion.apilogistica.controller;


import erp.gestion.apilogistica.entity.Unidad;
import erp.gestion.apilogistica.service.impl.UnidadServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("api/unidades")
public class UnidadController {
	
    private final UnidadServiceImpl service;

    public UnidadController(UnidadServiceImpl service){
        this.service=service;
    }
    
    @GetMapping
    public ResponseEntity<List<Unidad>> findAll(){
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Unidad> findById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }
    
    @PostMapping
    public ResponseEntity<Unidad> create(@Valid @RequestBody Unidad obj){
        return new ResponseEntity<>(service.create(obj), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Unidad> update(@PathVariable String id, @Valid @RequestBody Unidad obj) {
        return ResponseEntity.ok(service.update(id, obj));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
