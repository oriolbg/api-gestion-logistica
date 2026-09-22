package erp.gestion.apilogistica.mapper;



import erp.gestion.apilogistica.dto.ProductoDTO;
import erp.gestion.apilogistica.entity.Producto;
import erp.gestion.apilogistica.entity.Unidad;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ProductoMapper extends GenericMapper<Producto, ProductoDTO>{
    @Override
    public ProductoDTO toDTO(Producto entity) {
        if (entity == null) {
            return null;
        }
        return ProductoDTO.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .descripcion(entity.getDescripcion())
                .unidadId(entity.getUnidad() != null ? entity.getUnidad().getId() : null)
                .unidadNombre(entity.getUnidad() != null ? entity.getUnidad().getDescripcion() : null)
                .codImp(entity.getCodImp())
                .precioUnitario(entity.getPrecioUnitario())
                .fechaAlta(entity.getFechaAlta())
                .activo(entity.isActivo())
                .build();
    }

    @Override
    public Producto toEntity(ProductoDTO dto) {
        if(dto==null){
            return null;
        }
        return Producto.builder()
                .id(dto.getId())
                .codigo(dto.getCodigo())
                .descripcion(dto.getDescripcion())
                .precioUnitario(dto.getPrecioUnitario())
                .unidad(dto.getUnidadId() != null ? Unidad.builder().id(dto.getUnidadId()).build() : null)
                .codImp(dto.getCodImp())
                .fechaAlta(dto.getFechaAlta() != null ? dto.getFechaAlta() : LocalDate.now())
                .activo(dto.isActivo())
                .build();
    }
}
