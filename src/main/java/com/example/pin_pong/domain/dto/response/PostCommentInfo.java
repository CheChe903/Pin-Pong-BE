package com.example.pin_pong.domain.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostCommentInfo {

    private Long commentId;
    private Boolean selected;
    private String content;
    private Long postId;
    private Long memberId; // Optional: If you want to include member information

    // You can add more fields here as needed

}
