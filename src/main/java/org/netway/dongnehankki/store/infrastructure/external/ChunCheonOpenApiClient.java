package org.netway.dongnehankki.store.infrastructure.external;

import java.net.URI;

import org.netway.dongnehankki.store.exception.OpenApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChunCheonOpenApiClient {
	private final RestClient restClient;

	@Value("${openapi.cc.key}")
	private String apiKey;
	@Value("${openapi.cc.url}")
	private String baseUrl;


	public String fetchStoreData(int pageIndex, int pageSize) {
		try{
			URI uri = UriComponentsBuilder.fromUriString(baseUrl)
				.queryParam("serviceKey", apiKey)
				.queryParam("page", pageIndex)
				.queryParam("perPage", pageSize)
				.build().toUri();

			return restClient.get()
				.uri(uri)
				.retrieve()
				.body(String.class);
		} catch (Exception e) {
			throw new OpenApiException();
		}
	}
}