package org.netway.dongnehankki.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Value("${server.url}")
	private String serverUrl;

	@Bean
	public OpenAPI openAPI(){
		Server server = new Server();
		server.setUrl(serverUrl);

		return new OpenAPI()
			.components(new Components())
			.info(apiInfo())
			.addServersItem(server);
	}

	private Info apiInfo(){
		return new Info()
			.title("DongneHankki+ API")
			.description("DongneHankki+ API Documentation")
			.version("1.0");
	}
}

