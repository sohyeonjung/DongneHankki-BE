package org.netway.dongnehankki.post.application;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VertexAIServiceImpl implements VertexAIService{

	private final Client vertexClient;
	private final String modelId = "gemini-2.5-flash";

	public String generatePost(MultipartFile image, String text) {
		try {
			byte[] imageBytes = image.getBytes();

			Content content = Content.fromParts(
				Part.fromText(text),
				Part.fromBytes(imageBytes, image.getContentType())
			);

			GenerateContentResponse response = vertexClient.models.generateContent(
				modelId,
				content,
				null
			);

			return response.text();

		} catch (IOException e) {
			throw new RuntimeException("이미지 처리 중 오류 발생", e);
		}
	}

	public String generateText(String prompt) {
		Content content = Content.fromParts(Part.fromText(prompt));
		GenerateContentResponse response = vertexClient.models.generateContent(
			modelId,
			content,
			null
		);
		return response.text();
	}

}
