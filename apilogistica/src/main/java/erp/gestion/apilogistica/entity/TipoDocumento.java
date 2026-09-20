package erp.gestion.apilogistica.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoDocumento {
	DNI("DNI", 8),
	RUC("RUC", 11),
	CE("Carnet de Extranjeria", 12),
	PASAPORTE("Pasaporte", 20);
	
	private final String descripcion;
	private final int maxLength;

}
