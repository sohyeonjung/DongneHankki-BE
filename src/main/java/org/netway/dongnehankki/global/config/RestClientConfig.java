package org.netway.dongnehankki.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RestClientConfig {

	@Bean
	public RestClient restClient(){

		return RestClient.builder()
			.defaultStatusHandler(
				statusCode -> statusCode.is4xxClientError() || statusCode.is5xxServerError(),
				(request, response) -> {
					log.error("Exceplogtion Request: {} {}", request.getMethod(), request.getURI());
					if (response.getStatusCode().is4xxClientError()) {
						log.error("Client Exception Response: {} {}", response.getStatusCode(), response.getStatusText());
						throw new RuntimeException("Client exception");
					}
					if (response.getStatusCode().is5xxServerError()) {
						throw new RuntimeException("Server exception");
					}
					throw new RestClientException("Unexpected response status: " + response.getStatusCode());
				}
			)
			.build();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
