package erp.gestion.apilogistica.validator;


import erp.gestion.apilogistica.dto.ClienteDTO;
import erp.gestion.apilogistica.exception.ValidateException;

public class ClienteValidator {
	 public static void save(ClienteDTO registro) {
	        if (registro.getNombre() == null || registro.getNombre().trim().isEmpty()) {
	            throw new ValidateException("El nombre del cliente es requerido");
	        }
	        if (registro.getNumeroDocumento() == null || registro.getNumeroDocumento().trim().isEmpty()) {
	            throw new ValidateException("El numero de documento es requerido");
	        }
	        if (registro.getDescripcionDocumento() == null  || registro.getDescripcionDocumento().trim().isEmpty()) {
	            throw new ValidateException("El tipo de documento es requerido");
	        }
	    }
}
