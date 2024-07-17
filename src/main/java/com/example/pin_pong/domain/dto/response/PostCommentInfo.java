package com.example.pin_pong.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PostCommentInfo {

    private Long commentId;
    private Boolean selected;
    private String content;
    private Long postId;
    private String githubId;
    private String githubImage;
    // Optional: If you want to include member information
}
