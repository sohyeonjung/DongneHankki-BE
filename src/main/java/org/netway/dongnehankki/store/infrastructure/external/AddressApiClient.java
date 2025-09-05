package org.netway.dongnehankki.store.infrastructure.external;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AddressApiClient {

	private final RestClient restClient;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Value("${openapi.vworld.key}")
	private String apiKey;
	@Value("${openapi.vworld.url}")
	private String baseUrl;

	public double[] getCoordinates(String address) throws Exception {
		URI uri = UriComponentsBuilder.fromUriString(baseUrl)
			.queryParam("service", "address")
			.queryParam("request", "getcoord")
			.queryParam("version", "2.0")
			.queryParam("crs", "epsg:4326")
			.queryParam("address", address)
			.queryParam("refine", "true")
			.queryParam("simple", "false")
			.queryParam("format", "json")
			.queryParam("type", "road")
			.queryParam("key", apiKey)
			.encode(StandardCharsets.UTF_8)
			.build()
			.toUri();

		String response = restClient.get()
			.uri(uri)
			.retrieve()
			.body(String.class);

		JsonNode root = objectMapper.readTree(response);
		JsonNode pointNode = root.path("response").path("result").path("point");

		double lon = pointNode.path("x").asDouble();
		double lat = pointNode.path("y").asDouble();

		return new double[]{lat, lon};
	}
}
