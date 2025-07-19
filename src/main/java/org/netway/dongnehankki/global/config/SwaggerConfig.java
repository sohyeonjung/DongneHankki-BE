package org.netway.dongnehankki.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@Configuration
public class SwaggerConfig {

	@Value("${server.url}")
	private String serverUrl;

	@Bean
	public OpenAPI openAPI(){
		Server server = new Server();
		server.setUrl(serverUrl);

		// SecurityScheme 정의
		SecurityScheme securityScheme = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT");

		// SecurityRequirement 정의 (전역 적용)
		SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

		return new OpenAPI()
			.components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
			.info(apiInfo())
			.addServersItem(server)
			.addSecurityItem(securityRequirement); // SecurityRequirement 추가
	}

	private Info apiInfo(){
		return new Info()
			.title("DongneHankki+ API")
			.description("DongneHankki+ API Documentation")
			.version("1.0");
	}
}

