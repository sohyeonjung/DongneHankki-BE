package org.netway.dongnehankki.post.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class PostCreateRequest {

    private Long storeId;

    private String content;

    private MultipartFile[] images;

    private List<String> hashtags;

}
