package com.example.pin_pong.controller;

import com.example.pin_pong.domain.Member;
import com.example.pin_pong.domain.dto.response.MemberGithubIdInfo;
import com.example.pin_pong.domain.dto.response.MemberGithubImageInfo;
import com.example.pin_pong.domain.dto.response.MemberPinInfo;
import com.example.pin_pong.domain.dto.response.MemberTechStackInfo;
import com.example.pin_pong.service.MemberService;
import com.example.pin_pong.support.ApiResponse;
import com.example.pin_pong.support.ApiResponseGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/member")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/githubid")
    public ApiResponse<ApiResponse.SuccessBody<MemberGithubIdInfo>> getGithubId(HttpServletRequest request){
        Long memberId = memberService.findMemberByToken(request);
        Member member = memberService.findById(memberId);

        MemberGithubIdInfo res = MemberGithubIdInfo.builder()
                .githubId(member.getGithubId())
                .build();
        return ApiResponseGenerator.success(res, HttpStatus.OK);
    }

    @GetMapping("/githubimage")
    public ApiResponse<ApiResponse.SuccessBody<MemberGithubImageInfo>> getGithubImage(HttpServletRequest request){
        Long memberId = memberService.findMemberByToken(request);
        Member member = memberService.findById(memberId);

        MemberGithubImageInfo res = MemberGithubImageInfo.builder()
                .githubImage(member.getGithubImage())
                .build();
        return ApiResponseGenerator.success(res, HttpStatus.OK);
    }

    @GetMapping("/pin")
    public ApiResponse<ApiResponse.SuccessBody<MemberPinInfo>> getPin(HttpServletRequest request){
        Long memberId = memberService.findMemberByToken(request);
        Member member = memberService.findById(memberId);

        MemberPinInfo res = MemberPinInfo.builder()
                .pin(member.getPin())
                .build();
        return ApiResponseGenerator.success(res, HttpStatus.OK);
    }

    @GetMapping("/techstack")
    public ApiResponse<ApiResponse.SuccessBody<MemberTechStackInfo>> getTechStacks(HttpServletRequest request){
        Long memberId = memberService.findMemberByToken(request);
        Member member = memberService.findById(memberId);

        MemberTechStackInfo res = MemberTechStackInfo.builder()
                .techStacks(member.getTechStacks())
                .build();
        return ApiResponseGenerator.success(res, HttpStatus.OK);
    }
}
