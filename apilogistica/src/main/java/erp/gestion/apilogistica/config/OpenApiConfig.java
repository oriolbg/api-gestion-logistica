package erp.gestion.apilogistica.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
		info = @Info(
				title = "API REST de Gestión de Pedidos",
				version = "1.0",
				description = "Documentación de API REST de Gestión de Pedidos"
		)	
)
public class OpenApiConfig {

	//Sirve para comunicar a la API que debe añadir la informacion referente al requisito de autenticacion por Tokens JWT en el Header
	@Bean
	public OpenAPI customOpenAPI() {
		final String securitySchemeName = "bearerAuth";
		
		return new OpenAPI()
				.addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
				.components(new Components()
							.addSecuritySchemes(securitySchemeName, new SecurityScheme()
																		.name(securitySchemeName)
																		.type(SecurityScheme.Type.HTTP)
																		.scheme("bearer")
																		.bearerFormat("JWT")));
	}
}
