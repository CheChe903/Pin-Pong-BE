package com.example.pin_pong.domain.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MemberRankingInfo {
    private String githubId;
    private String githubImage;
    private int pin;
    private List<String> techStacks;
}
