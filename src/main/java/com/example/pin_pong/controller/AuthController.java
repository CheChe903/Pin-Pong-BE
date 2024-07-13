package com.example.pin_pong.controller;

import com.example.pin_pong.domain.dto.response.URLGithubInfo;
import com.example.pin_pong.service.GithubService;
import com.example.pin_pong.service.TokenService;
import com.example.pin_pong.support.ApiResponse;
import com.example.pin_pong.support.ApiResponseGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpSession;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class AuthController {

    @Autowired
    private GithubService githubService;

    @Autowired
    private TokenService tokenService;

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
    public RedirectView handleGithubCallback(@RequestParam("code") String code, HttpSession session) {
        String accessToken = githubService.getAccessToken(code);
        Map<String, Object> userInfo = githubService.getUserInfo(accessToken);

        log.debug(userInfo.toString());

//        Member member = memberService.saveOrUpdateUser(githubProfile);
//        String jwtToken = tokenService.createToken(member.getId());


        // 리다이렉션할 URL 생성
        String redirectUrl = redirectUri;  // 예시: 리다이렉션할 경로

        return new RedirectView(redirectUrl);
    }

    @GetMapping("/generate-token") // Server token 생성
    public String generateToken(@RequestParam Long id) {
        return tokenService.createToken(id);
    }
}
