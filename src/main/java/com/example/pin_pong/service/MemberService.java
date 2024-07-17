package com.example.pin_pong.service;

import com.example.pin_pong.domain.Member;
import com.example.pin_pong.domain.TechStack;
import com.example.pin_pong.domain.dto.response.MemberRankingInfo;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

        // 핀은 기본값으로 설정 (예: 20)
        Integer pin = 5;

        log.debug("githubId: {}", githubId);
        log.debug("githubImage: {}", githubImage);

        Member member = memberRepository.findByGithubId(githubId).orElse(null);

        LocalDateTime now = LocalDateTime.now();

        if (member == null) {
            log.debug("Member not found. Creating new member.");
            member = Member.builder()
                    .githubId(githubId)
                    .githubImage(githubImage)
                    .techStacks(techStacks)
                    .pin(pin)
                    .lastLogin(now)
                    .build();
            member = memberRepository.save(member);
            increaseDailyPin(member.getId());
            log.debug("Saved new member with id: {}", member.getId());
        } else {
            log.debug("Member found with id: {}", member.getId());

            if (member.getLastLogin() == null || !member.getLastLogin().toLocalDate().isEqual(LocalDate.now())) {
                log.debug("Last login was not today. Logging the event.");
                // 추가 로직을 여기에 추가할 수 있습니다.
                increaseDailyPin(member.getId());
            }
            // 이미 존재하는 멤버의 경우 업데이트 로직을 추가할 수 있음
        }
        member.setLastLogin(now);
        member = memberRepository.save(member);

        return member;
    }

    private String extractUserIdFromProfile(String githubProfile) {
        try {
            JsonNode jsonNode = objectMapper.readTree(githubProfile);
            return String.valueOf(jsonNode.path("owner").path("id").asText());
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
        int pin_inc = 10;
        member.setPin(member.getPin() + pin_inc);
        memberRepository.save(member);
        log.debug("Increased pin for member with id: {}", memberId);
    }

    public void increaseDailyPin(Long memberId) {
        Member member = findById(memberId);
        int pin_inc = 1;
        member.setPin(member.getPin() + pin_inc);
        memberRepository.save(member);
        log.debug("Increased daily pin for member with id: {}", memberId);
    }

    public Set<String> getMemberTechStackNames(Long memberId) {
        Member member = findById(memberId);
        return member.getTechStacks().stream()
                .map(TechStack::getTechName)
                .collect(Collectors.toSet());
    }

    public List<MemberRankingInfo> getMemberRanking() {
        return memberRepository.findAll().stream()
                .sorted((m1, m2) -> Integer.compare(m2.getPin(), m1.getPin()))
                .map(member -> MemberRankingInfo.builder()
                        .githubId(member.getGithubId())
                        .githubImage(member.getGithubImage())
                        .pin(member.getPin())
                        .techStacks(member.getTechStacks().stream()
                                .map(techStack -> techStack.getTechName())
                                .sorted()
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }
}
