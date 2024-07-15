package com.example.pin_pong.controller;

import com.example.pin_pong.domain.Member;
import com.example.pin_pong.domain.dto.response.TokenResponseInfo;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.HashMap;
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
    @ResponseBody
    public ResponseEntity<TokenResponseInfo> handleGithubCallback(@RequestParam("code") String code, HttpSession session, RedirectAttributes redirectAttributes) {
        String accessToken = githubService.getAccessToken(code);
        Map<String, Object> userInfo = githubService.getUserInfo(accessToken);

        log.debug("USERINFO : {}", userInfo.toString());

        // GitHub 사용자의 avatar_url과 id 정보 추출
        String githubImage = (String) userInfo.get("avatar_url");
        String githubId = (String) userInfo.get("login");

        // GitHub 사용자의 프로필 JSON 생성
        String githubProfile = String.format("{\"owner\": {\"avatar_url\": \"%s\", \"id\": \"%s\"}}", githubImage, githubId);


        Member member = memberService.saveOrUpdateUser(githubProfile);
        String jwtToken = tokenService.createToken(member.getId());

        // 응답 DTO 생성
        TokenResponseInfo response = new TokenResponseInfo(jwtToken, accessToken, githubId);


        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/generate-token")
    public ResponseEntity<Map<String, String>> generateToken(@RequestParam("id") Long id) {
        String jwtToken = tokenService.createToken(id);
        return ResponseEntity.ok(Collections.singletonMap("token", jwtToken));
    }
}
