package com.example.pin_pong.service;

import com.example.pin_pong.domain.Member;
import com.example.pin_pong.domain.TechStack;
import com.example.pin_pong.repository.MemberRepository;
import com.example.pin_pong.repository.TechStackRepository;
import com.example.pin_pong.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final TechStackRepository techStackRepository;
    private final JwtTokenProvider jwtTokenProvider;  // JwtTokenProvider 추가

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

    public Member findById(Long id) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        return optionalMember.orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원을 찾을 수 없습니다."));
    }

    public Member findByGithubId(String githubId) {
        return memberRepository.findByGithubId(githubId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with GitHub ID: " + githubId));
    }

    public Long findMemberByToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            return jwtTokenProvider.getIdFromToken(token);
        } else {
            throw new IllegalArgumentException("Invalid or missing Authorization header");
        }
    }

    public Member updateTechStacks(Long memberId, Set<String> techStackNames) {
        Member member = findById(memberId);
        Set<TechStack> techStacks = new HashSet<>();

        for (String techName : techStackNames) {
            TechStack techStack = techStackRepository.findByTechName(techName)
                    .orElseGet(() -> techStackRepository.save(new TechStack(techName)));
            techStacks.add(techStack);
        }

        member.getTechStacks().clear();
        member.getTechStacks().addAll(techStacks);
        return memberRepository.save(member);
    }

    public void decreasePin(Long memberId) {
        Member member = findById(memberId);
        if (member.getPin() <= 0) {
            throw new IllegalArgumentException("Pin should be at least 1");
        } else {
            member.setPin(member.getPin() - 1);
            memberRepository.save(member);
            log.debug("Decreased pin for member with id: {}", memberId);
        }
    }

    public void increasePin(Long memberId) {
        Member member = findById(memberId);
        Integer pin_inc = 3;
        member.setPin(member.getPin() + pin_inc);
        memberRepository.save(member);
        log.debug("Increased pin for member with id: {}", memberId);
    }

}
