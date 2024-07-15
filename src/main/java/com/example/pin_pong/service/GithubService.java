package com.example.pin_pong.service;

import com.example.pin_pong.domain.dto.response.GithubCommit;
import com.example.pin_pong.domain.dto.response.GithubCommitDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GithubService {

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.client-secret}")
    private String clientSecret;

    @Autowired
    private final RestTemplate restTemplate;

    public GithubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAccessToken(String code) {
        String tokenUrl = "https://github.com/login/oauth/access_token";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, new ParameterizedTypeReference<Map<String, Object>>() {});
        Map<String, Object> responseBody = response.getBody();
        return (String) responseBody.get("access_token");
    }

    public Map<String, Object> getUserInfo(String accessToken) {
        String userInfoUrl = "https://api.github.com/user";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Accept", "application/json");

        HttpEntity<String> userEntity = new HttpEntity<>(headers);
        ResponseEntity<Map<String, Object>> userResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, userEntity, new ParameterizedTypeReference<Map<String, Object>>() {});

        return userResponse.getBody();
    }

    public Map<String, String> getCommitsAndPatches(String githubRepoUrl, String accessToken) {
        String apiUrl = convertToApiUrl(githubRepoUrl);
        String commitsUrl = apiUrl + "/commits";

        Map<String, String> commitMap = new HashMap<>();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<GithubCommit>> response = restTemplate.exchange(
                commitsUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<GithubCommit>>() {}
        );

        List<GithubCommit> commits = response.getBody();

        if (commits != null) {
            for (GithubCommit commit : commits) {
                String commitId = commit.getSha();
                String patchUrl = apiUrl + "/commits/" + commitId;

                ResponseEntity<GithubCommitDetail> commitDetailResponse = restTemplate.exchange(
                        patchUrl,
                        HttpMethod.GET,
                        entity,
                        GithubCommitDetail.class
                );

                GithubCommitDetail commitDetail = commitDetailResponse.getBody();
                String patch = commitDetail != null ? commitDetail.getPatch() : "";

                commitMap.put(commitId, patch);
            }
        }

        return commitMap;
    }

    private String convertToApiUrl(String githubRepoUrl) {
        return githubRepoUrl.replace("https://github.com/", "https://api.github.com/repos/")
                .replace("/pull/", "/pulls/");
    }
}
