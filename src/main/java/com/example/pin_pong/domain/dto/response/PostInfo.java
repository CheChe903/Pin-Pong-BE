package com.example.pin_pong.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.example.pin_pong.domain.Member;
import com.example.pin_pong.domain.TechStack;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PostInfo {
    private String postTitle;
    private String content;
    private String authorGithubId;
    private String authorGithubImage;
    private Long prId;
    private Map<String, String> commitList;
    private Set<TechStack> techStacks;
    private Set<String> likedMembersGithubId;
    private List<PostCommentInfo> comments;
}
