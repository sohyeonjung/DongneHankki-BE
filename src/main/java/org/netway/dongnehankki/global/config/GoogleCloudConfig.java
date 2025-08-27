package org.netway.dongnehankki.global.config;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.genai.Client;
import com.google.genai.types.HttpOptions;

@Configuration
public class GoogleCloudConfig {

	@Value("classpath:googlecloudkey.json")
	Resource gcsCredentials;
	@Value("${google.cloud.project}")
	private String projectId;
	@Value("${google.cloud.location}")
	private String location;

	@Bean
	public Client vertexClient() throws IOException {
		List<String> scopes = Collections.singletonList("https://www.googleapis.com/auth/cloud-platform");

		GoogleCredentials credentials = GoogleCredentials.fromStream(gcsCredentials.getInputStream())
			.createScoped(scopes);

		return Client.builder()
			.credentials(credentials)
			.project(projectId)
			.location(location)
			.vertexAI(true)
			.httpOptions(HttpOptions.builder().apiVersion("v1").build())
			.build();
	}
}
