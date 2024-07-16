package com.example.pin_pong.domain.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentWriteRequest {
    private String content;
}
