package com.example.pin_pong.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class PostWriteRequest {
    private String postTitle;
    private String githubRepoUrl;
    private Map<Long, String> commitList;
    private String content;
    private Set<String> techStacks;

    public Long extractPrIdFromGithubUrl() {
        // 예시 URL: https://github.com/KimJinYeongZ/Pin-Pong-BE/pull/1
        String[] parts = githubRepoUrl.split("/");
        String lastPart = parts[parts.length - 1];
        return Long.parseLong(lastPart); // assuming the last part is the PR ID
    }
}
