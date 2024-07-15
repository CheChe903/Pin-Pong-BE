package com.example.pin_pong.controller;

import com.example.pin_pong.domain.Member;
import com.example.pin_pong.domain.dto.request.UpdateTechStacksRequest;
import com.example.pin_pong.domain.dto.response.MemberGithubIdInfo;
import com.example.pin_pong.domain.dto.response.MemberGithubImageInfo;
import com.example.pin_pong.domain.dto.response.MemberPinInfo;
import com.example.pin_pong.domain.dto.response.MemberTechStacksInfo;
import com.example.pin_pong.service.MemberService;
import com.example.pin_pong.support.ApiResponse;
import com.example.pin_pong.support.ApiResponseGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/member")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

//    @GetMapping("/githubid")
//    public ApiResponse<ApiResponse.SuccessBody<MemberGithubIdInfo>> getGithubId(HttpServletRequest request){
//        Long memberId = memberService.findMemberByToken(request);
//        Member member = memberService.findById(memberId);
//
//        MemberGithubIdInfo res = MemberGithubIdInfo.builder()
//                .githubId(member.getGithubId())
//                .build();
//        return ApiResponseGenerator.success(res, HttpStatus.OK);
//    }

    @GetMapping("/githubimage/{githubId}")
    public ApiResponse<ApiResponse.SuccessBody<MemberGithubImageInfo>> getGithubImage(@PathVariable("githubId") String githubId) {
        Member member = memberService.findByGithubId(githubId);

        MemberGithubImageInfo res = MemberGithubImageInfo.builder()
                .githubImage(member.getGithubImage())
                .build();

        return ApiResponseGenerator.success(res, HttpStatus.OK);
    }

    @GetMapping("/pin/{githubId}")
    public ApiResponse<ApiResponse.SuccessBody<MemberPinInfo>> getPin(@PathVariable("githubId") String githubId) {
        Member member = memberService.findByGithubId(githubId);

        MemberPinInfo res = MemberPinInfo.builder()
                .pin(member.getPin())
                .build();

        return ApiResponseGenerator.success(res, HttpStatus.OK);
    }

    @GetMapping("/techstacks/{githubId}")
    public ApiResponse<ApiResponse.SuccessBody<MemberTechStacksInfo>> getTechStacks(@PathVariable("githubId") String githubId) {
        Member member = memberService.findByGithubId(githubId);

        MemberTechStacksInfo res = MemberTechStacksInfo.builder()
                .techStacks(member.getTechStacks())
                .build();

        return ApiResponseGenerator.success(res, HttpStatus.OK);
    }

    @PatchMapping("/techstacks/update")
    public ApiResponse<ApiResponse.SuccessBody<MemberTechStacksInfo>> updateTechStacks(@RequestBody UpdateTechStacksRequest updateRequest, HttpServletRequest request) {
        Long memberId = memberService.findMemberByToken(request);
        Member updatedMember = memberService.updateTechStacks(memberId, updateRequest.getTechStacks());

        MemberTechStacksInfo res = MemberTechStacksInfo.builder()
                .techStacks(updatedMember.getTechStacks())
                .build();
        return ApiResponseGenerator.success(res, HttpStatus.OK);
    }
}
