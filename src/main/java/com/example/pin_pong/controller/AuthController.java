package com.example.pin_pong.controller;

import com.example.pin_pong.domain.Member;
import com.example.pin_pong.domain.dto.response.URLGithubInfo;
import com.example.pin_pong.service.GithubService;
import com.example.pin_pong.service.MemberService;
import com.example.pin_pong.service.TokenService;
import com.example.pin_pong.support.ApiResponse;
import com.example.pin_pong.support.ApiResponseGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api/v1")
public class AuthController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GithubService githubService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private MemberService memberService;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.scope}")
    private String scope;

    @Value("${github.client.redirect-uri}")
    private String redirectUri;

    @GetMapping("/login")
    public ApiResponse<ApiResponse.SuccessBody<URLGithubInfo>> returnGithubAuthenticateURL() {
        String url = "https://github.com/login/oauth/authorize" + "?" +
                "client_id=" + clientId + "&" +
                "scope=" + scope + "&" +
                "allow_signup=true";

        URLGithubInfo res = URLGithubInfo.builder()
                .url(url)
                .build();

        return ApiResponseGenerator.success(res, HttpStatus.OK);
    }

    @GetMapping("/oauth/github/callback")
    public ModelAndView handleGithubCallback(@RequestParam("code") String code, HttpSession session, RedirectAttributes redirectAttributes) {
        String accessToken = githubService.getAccessToken(code);
        Map<String, Object> userInfo = githubService.getUserInfo(accessToken);

        log.debug(userInfo.toString());

        // GitHub 사용자의 avatar_url과 id 정보 추출
        String githubImage = (String) userInfo.get("avatar_url");
        Long githubId = Long.parseLong((String) userInfo.get("id"));

        // GitHub 사용자의 프로필 JSON 생성
        String githubProfile = String.format("{\"owner\": {\"avatar_url\": \"%s\", \"id\": \"%d\"}}", githubImage, githubId);

        Member member = memberService.saveOrUpdateUser(githubProfile);
        String jwtToken = tokenService.createToken(member.getId());

        // 토큰을 RedirectAttributes에 추가하여 리다이렉션 시 전달
        redirectAttributes.addAttribute("token", jwtToken);

        // 리다이렉션할 URL 생성
        String redirectUrl = redirectUri;  // 예시: 리다이렉션할 경로

        return new ModelAndView(new RedirectView(redirectUrl));
    }

    @GetMapping("/generate-token") // Server token 생성
    public String generateToken(@RequestParam Long id) {
        return tokenService.createToken(id);
    }
}
