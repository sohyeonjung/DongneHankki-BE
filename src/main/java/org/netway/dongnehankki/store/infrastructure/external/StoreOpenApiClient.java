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
public class StoreOpenApiClient {
	private final RestClient restClient;

	@Value("${openapi.gg.key}")
	private String apiKey;
	@Value("${openapi.gg.url}")
	private String baseUrl;
	@Value("${openapi.gg.sigun-nm}")
	private String sigunNm;

	public String fetchStoreData(int pageIndex, int pageSize) {
		try{
			URI uri = UriComponentsBuilder.fromUriString(baseUrl)
				.queryParam("KEY", apiKey)
				.queryParam("Type", "json")
				.queryParam("pIndex", pageIndex)
				.queryParam("pSize", pageSize)
				.queryParam("SIGUN_NM", sigunNm)
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
