package org.netway.dongnehankki.post.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostCreateRequest {

    private Long storeId;

    private String content;

    private List<String> imageUrls;

    private List<String> hashtags;

}
