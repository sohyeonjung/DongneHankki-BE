package org.netway.dongnehankki.post.application;

import org.springframework.web.multipart.MultipartFile;

public interface VertexAIService {
    String generatePost(MultipartFile image, String text);
    String generateText(String prompt);
}
