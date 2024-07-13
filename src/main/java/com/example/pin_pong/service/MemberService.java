package com.example.pin_pong.service;

import com.example.pin_pong.domain.Member;
import com.example.pin_pong.domain.TechStack;
import com.example.pin_pong.repository.MemberRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public Member saveOrUpdateUser(String githubProfile) {
        // 프로필 JSON 파싱
        String githubId = extractUserIdFromProfile(githubProfile);
        String githubImage = extractGithubImageFromProfile(githubProfile);

        // 기술 스택은 임시로 빈 Set으로 설정
        Set<TechStack> techStacks = new HashSet<>();

        // 핀은 기본값으로 설정 (예: 0)
        Integer pin = 0;

        log.debug("githubId: {}", githubId);
        log.debug("githubImage: {}", githubImage);

        Member member = memberRepository.findByGithubId(githubId).orElse(null);

        if (member == null) {
            log.debug("Member not found. Creating new member.");
            member = Member.builder()
                    .githubId(githubId)
                    .githubImage(githubImage)
                    .techStacks(techStacks)
                    .pin(pin)
                    .build();
            member = memberRepository.save(member);
            log.debug("Saved new member with id: {}", member.getId());
        } else {
            log.debug("Member found with id: {}", member.getId());
            // 이미 존재하는 멤버의 경우 업데이트 로직을 추가할 수 있음
        }

        return member;
    }

    private String extractUserIdFromProfile(String githubProfile) {
        try {
            JsonNode jsonNode = objectMapper.readTree(githubProfile);
            return String.valueOf(jsonNode.path("owner").path("id").asLong());
        } catch (Exception e) {
            log.error("Failed to extract userId from profile", e);
            return null;
        }
    }

    private String extractGithubImageFromProfile(String githubProfile) {
        try {
            JsonNode jsonNode = objectMapper.readTree(githubProfile);
            return jsonNode.path("owner").path("avatar_url").asText();
        } catch (Exception e) {
            log.error("Failed to extract github image from profile", e);
            return null;
        }
    }
}
