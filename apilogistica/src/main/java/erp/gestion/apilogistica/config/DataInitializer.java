package erp.gestion.apilogistica.config;


import erp.gestion.apilogistica.entity.Rol;
import erp.gestion.apilogistica.entity.TipoComprobante;
import erp.gestion.apilogistica.entity.Unidad;
import erp.gestion.apilogistica.repository.RolRepository;
import erp.gestion.apilogistica.repository.TipoComprobanteRepository;
import erp.gestion.apilogistica.repository.UnidadRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
	
	@SuppressWarnings("unused")
	@Bean
	CommandLineRunner initData(RolRepository usuarioRepo, TipoComprobanteRepository tipoCompRepo, UnidadRepository unidadRepo) {
		return args -> {
			//Roles usuarios
			insertarRol(usuarioRepo, 1, "ADMIN");
			insertarRol(usuarioRepo, 2, "USER");
			
			//Tipos de Comprobante
			insertarTipoComprobante(tipoCompRepo, "00", "Otros");
			insertarTipoComprobante(tipoCompRepo, "01", "Factura");
			insertarTipoComprobante(tipoCompRepo, "03", "Boleta Venta");
			insertarTipoComprobante(tipoCompRepo, "07", "Nota de Crédito");
			insertarTipoComprobante(tipoCompRepo, "08", "Nota de Débito");
			insertarTipoComprobante(tipoCompRepo, "09", "Guia de Remision - Remitente");
			
			//Unidades de Ejemplo
			insertarUnidad(unidadRepo, "KG", "Kilogramo");
			insertarUnidad(unidadRepo, "LT", "Litro");
			insertarUnidad(unidadRepo, "BO", "Botella");
			insertarUnidad(unidadRepo, "BX", "Caja");
		};
	}
	
	private void insertarRol(RolRepository repo, Integer id, String nombre) {
		if(!repo.existsById(id)) {
			repo.save(Rol.builder()
					.nombre(nombre)
					.build());
		}	
	}

	private void insertarTipoComprobante(TipoComprobanteRepository repo, String codigo, String descripcion) {
		repo.findByCodigo(codigo).orElseGet(() -> repo.save(TipoComprobante.builder()
													.codigo(codigo)
													.descripcion(descripcion)
													.build())
												);
	}
	
	private void insertarUnidad(UnidadRepository repo, String id, String descripcion) {
		if(!repo.existsById(id)) {
			repo.save(Unidad.builder()
					.id(id)
					.descripcion(descripcion)
					.build());
		}	
	}
}
