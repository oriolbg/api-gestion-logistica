package erp.gestion.apilogistica.validator;


import erp.gestion.apilogistica.dto.ProductoDTO;
import erp.gestion.apilogistica.exception.ValidateException;

public class ProductoValidator {
    public static void save(ProductoDTO registro) {
        if (registro.getDescripcion() == null || registro.getDescripcion().trim().isEmpty()) {
            throw new ValidateException("El nombre del producto es requerido");
        }
        if (registro.getUnidadId() == null || registro.getUnidadId().trim().isEmpty()) {
            throw new ValidateException("La Unidad es requerida");
        }
        if (registro.getDescripcion().trim().length() > 70) {
            throw new ValidateException("El nombre del producto no debe exceder los 70 caracteres");
        }
        if (registro.getPrecioUnitario() == null || registro.getPrecioUnitario().doubleValue() < 0) {
            throw new ValidateException("El precio unitario debe ser mayor o igual que cero");
        }
        if (registro.getCodImp() == null || registro.getCodImp().trim().isEmpty()) {
            throw new ValidateException("El codigo de impuesto es requerido");
        }
    }
}
