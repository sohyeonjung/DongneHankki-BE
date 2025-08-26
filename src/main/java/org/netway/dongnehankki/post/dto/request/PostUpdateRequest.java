package org.netway.dongnehankki.post.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpdateRequest {
    private String content;
    private List<String> hashtags;
    private List<Long> deleteImageIds;
}
