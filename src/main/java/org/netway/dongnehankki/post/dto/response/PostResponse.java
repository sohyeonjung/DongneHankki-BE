package org.netway.dongnehankki.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.netway.dongnehankki.post.domain.Image;
import org.netway.dongnehankki.post.domain.Post;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostResponse {

    private final Long postId;
    private final String content;
    private final Long storeId;
    private final String storeName;
    private final Long userId;
    private final String userNickname;
    private final List<ImageResponse> images;
    private final List<String> hashtags;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    @Builder
    public PostResponse(Long postId, String content, Long storeId, String storeName, Long userId, String userNickname, List<ImageResponse> images, List<String> hashtags, LocalDateTime createdAt) {
        this.postId = postId;
        this.content = content;
        this.storeId = storeId;
        this.storeName = storeName;
        this.userId = userId;
        this.userNickname = userNickname;
        this.images = images;
        this.hashtags = hashtags;
        this.createdAt = createdAt;
    }

    public static PostResponse fromEntity(Post post) {
        return PostResponse.builder()
                .postId(post.getPostId())
                .content(post.getContent())
                .storeId(post.getStore().getStoreId())
                .storeName(post.getStore().getName())
                .userId(post.getUser().getUserId())
                .userNickname(post.getUser().getNickname())
                .images(post.getImages().stream()
                        .sorted(Comparator.comparingInt(Image::getDisplayOrder))
                        .map(ImageResponse::from)
                        .collect(Collectors.toList()))
                .hashtags(post.getPostHashtags().stream()
                        .map(postHashtag -> postHashtag.getHashtag().getName())
                        .collect(Collectors.toList()))
                .createdAt(post.getCreatedAt())
                .build();
    }

    @Getter
    public static class ImageResponse {
        private final String imageUrl;
        private final Long imageId;
        private final int displayOrder;

        @Builder
        public ImageResponse(String imageUrl, Long imageId, int displayOrder) {
            this.imageUrl = imageUrl;
            this.imageId = imageId;
            this.displayOrder = displayOrder;
        }

        public static ImageResponse from(Image image) {
            return ImageResponse.builder()
                    .imageId(image.getImageId())
                    .imageUrl(image.getUrl())
                    .displayOrder(image.getDisplayOrder())
                    .build();
        }
    }
}
