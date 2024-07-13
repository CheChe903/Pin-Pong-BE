package com.example.pin_pong.domain.dto.response;


import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PostLikeCountInfo {
    private int likeCount;
}

