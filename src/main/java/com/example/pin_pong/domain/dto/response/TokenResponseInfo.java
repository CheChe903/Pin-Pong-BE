package com.example.pin_pong.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenResponseInfo {
    private String serverAccessToken;
    private String githubAccessToken;
    private String githubId;  // 새로운 필드 추가
}
