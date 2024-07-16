package com.example.pin_pong.controller;

import com.example.pin_pong.domain.Comment;
import com.example.pin_pong.domain.Member;
import com.example.pin_pong.domain.Post;
import com.example.pin_pong.domain.TechStack;
import com.example.pin_pong.domain.dto.request.CommentWriteRequest;
import com.example.pin_pong.domain.dto.request.PostWriteRequest;
import com.example.pin_pong.domain.dto.response.*;
import com.example.pin_pong.service.*;
import com.example.pin_pong.support.ApiResponse;
import com.example.pin_pong.support.ApiResponseGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/api/v1/post")
public class PostController {

    private final PostService postService;
    private final MemberService memberService;
    private final TechStackService techStackService;
    private final TokenService tokenService;
    private final GithubService githubService;
    private final CommentService commentService;

    @Autowired
    public PostController(PostService postService, MemberService memberService, TechStackService techStackService, TokenService tokenService, GithubService githubService, CommentService commentService) {
        this.postService = postService;
        this.memberService = memberService;
        this.techStackService = techStackService;
        this.tokenService = tokenService;
        this.githubService = githubService;
        this.commentService = commentService;
    }

    @PostMapping("/write")
    public ApiResponse<ApiResponse.SuccessBody<Post>> writePost(@RequestBody PostWriteRequest postRequest, HttpServletRequest request) {
        Long memberId = memberService.findMemberByToken(request);
        Member author = memberService.findById(memberId);

        // Check if author's pin is sufficient
        if (author.getPin() > 0) {
            memberService.decreasePin(memberId);
        }

        String githubAccessToken = request.getHeader("githubAccessToken");
        log.debug("githubAccessToken : {}", githubAccessToken);

        // Github Repository URL에서 PR ID 추출
        Long prId = postRequest.extractPrIdFromGithubUrl();

        // GithubService를 통해 PR의 모든 커밋 ID와 패치 내용을 가져옴
        Map<String, String> commitList = githubService.getCommitsAndPatches(postRequest.getGithubRepoUrl(), githubAccessToken);

        log.debug("commitList : {}", commitList.toString());

        Set<TechStack> techStacks = postRequest.getTechStacks().stream()
                .map(techName -> techStackService.findByName(techName).orElseThrow(() -> new IllegalArgumentException("TechStack not found: " + techName)))
                .collect(Collectors.toSet());

        Post newPost = Post.builder()
                .postTitle(postRequest.getPostTitle())
                .prId(prId)
                .commitList(commitList)
                .content(postRequest.getContent())
                .author(author)
                .techStacks(techStacks)
                .likedMembers(new HashSet<>()) // Initialize likedMembers to an empty set
                .build();

        Post savedPost = postService.save(newPost);

        return ApiResponseGenerator.success(savedPost, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ApiResponse<ApiResponse.SuccessBody<PostInfo>> getPostById(@PathVariable("postId") Long postId) {
        Post post = postService.findById(postId);
        Member author = post.getAuthor();

        // Post와 관련된 모든 댓글 가져오기
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        List<PostCommentInfo> commentInfos = comments.stream()
                .map(comment -> PostCommentInfo.builder()
                        .commentId(comment.getId())
                        .selected(comment.getSelected())
                        .content(comment.getContent())
                        .githubId(comment.getMember().getGithubId()) // 수정된 부분
                        .build())
                .toList();

        Set<String> likedMemberIds = post.getLikedMembers().stream()
                .map(Member::getGithubId)
                .collect(Collectors.toSet());

        PostInfo postInfo = PostInfo.builder()
                .postTitle(post.getPostTitle())
                .content(post.getContent())
                .authorGithubId(author.getGithubId())
                .authorGithubImage(author.getGithubImage())
                .prId(post.getPrId())
                .commitList(post.getCommitList())
                .techStacks(post.getTechStacks())
                .likedMembersGithubId(likedMemberIds) // 변경된 부분
                .comments(commentInfos)
                .build();

        return ApiResponseGenerator.success(postInfo, HttpStatus.OK);
    }

    @PostMapping("/{postId}/like")
    public ApiResponse<ApiResponse.SuccessBody<Post>> likePost(@PathVariable("postId") Long postId, HttpServletRequest request) {
        Long memberId = memberService.findMemberByToken(request);
        Member member = memberService.findById(memberId);

        Post updatedPost = postService.addLike(postId, member);

        return ApiResponseGenerator.success(updatedPost, HttpStatus.OK);
    }

    @GetMapping("/{postId}/like/get")
    public ApiResponse<ApiResponse.SuccessBody<PostLikeCountInfo>> getLikeCount(@PathVariable("postId") Long postId) {

        PostLikeCountInfo res = PostLikeCountInfo.builder()
                .likeCount(postService.getLikeCount(postId))
                .build();
        return ApiResponseGenerator.success(res, HttpStatus.OK);

    }

    @PostMapping("/{postId}/comment/add")
    public ApiResponse<ApiResponse.SuccessBody<Comment>> addComment(@PathVariable("postId") Long postId, @RequestBody CommentWriteRequest commentWriteRequest, HttpServletRequest request) {
        Long memberId = memberService.findMemberByToken(request);
        Member member = memberService.findById(memberId);
        Post post = postService.findById(postId);

        Comment newComment = Comment.builder()
                .selected(Boolean.FALSE)
                .post(post)
                .member(member)
                .content(commentWriteRequest.getContent())
                .build();

        Comment savedComment = commentService.save(newComment);

        return ApiResponseGenerator.success(savedComment, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}/comment/get")
    public ApiResponse<ApiResponse.SuccessBody<List<PostCommentInfo>>> getCommentsByPostId(@PathVariable("postId") Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);


        List<PostCommentInfo> res = comments.stream()
                .map(comment -> PostCommentInfo.builder()
                        .commentId(comment.getId())
                        .selected(comment.getSelected())
                        .content(comment.getContent())
                        .githubId(comment.getMember().getGithubId()) // 수정된 부분
                        .build())
                .collect(Collectors.toList());

        return ApiResponseGenerator.success(res, HttpStatus.OK);
    }

    @PatchMapping("/{postId}/{commentId}/select")
    public ApiResponse<?> selectComment(@PathVariable("postId") Long postid, @PathVariable("commentId") Long commentid, HttpServletRequest request) {
        Long memberId = memberService.findMemberByToken(request);
        Member member = memberService.findById(memberId);

        Comment selectedComment = commentService.selectComment(commentid, member);

        if (selectedComment != null) {
            return ApiResponseGenerator.success(selectedComment, HttpStatus.OK);
        } else {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .message("Failed to select comment with id: " + commentid)
                    .build();
            return ApiResponseGenerator.success(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/list")
    public ApiResponse<ApiResponse.SuccessBody<List<PostListInfo>>> getAllPosts() {
        List<Post> posts = postService.findAllPosts();

        List<PostListInfo> postInfos = posts.stream()
                .map(post -> {
                    // 댓글 목록 가져오기
                    List<Comment> comments = commentService.getCommentsByPostId(post.getId());

                    // postSelected 계산
                    boolean postSelected = comments.stream().anyMatch(Comment::getSelected);

                    return PostListInfo.builder()
                            .postId(post.getId())
                            .postTitle(post.getPostTitle())
                            .githubId(post.getAuthor().getGithubId())
                            .githubImage(post.getAuthor().getGithubImage())
                            .likedMemberCount(post.getLikedMembers().size())
                            .postSelected(postSelected)
                            .techStacks(post.getTechStacks())
                            .build();
                })
                .collect(Collectors.toList());

        return ApiResponseGenerator.success(postInfos, HttpStatus.OK);
    }

    @GetMapping("/{githubId}/list")
    public ApiResponse<ApiResponse.SuccessBody<List<PostListInfo>>> getPostsByGithubId(@PathVariable("githubId") String githubId) {
        List<Post> posts = postService.findByAuthorGithubId(githubId);

        List<PostListInfo> postInfos = posts.stream()
                .map(post -> {
                    // 댓글 목록 가져오기
                    List<Comment> comments = commentService.getCommentsByPostId(post.getId());

                    // postSelected 계산
                    boolean postSelected = comments.stream().anyMatch(Comment::getSelected);

                    return PostListInfo.builder()
                            .postId(post.getId())
                            .postTitle(post.getPostTitle())
                            .githubId(post.getAuthor().getGithubId())
                            .githubImage(post.getAuthor().getGithubImage())
                            .likedMemberCount(post.getLikedMembers().size())
                            .postSelected(postSelected)
                            .techStacks(post.getTechStacks())
                            .build();
                })
                .collect(Collectors.toList());

        return ApiResponseGenerator.success(postInfos, HttpStatus.OK);
    }


    @GetMapping("/alltechstacks/list")
    public ApiResponse<ApiResponse.SuccessBody<TechStacksInfo>> getAllTechStacks() {
        List<TechStack> techStacks = techStackService.findAllTechStacks();

        TechStacksInfo res = TechStacksInfo.builder()
                .techStacks(new HashSet<>(techStacks)) // Convert List to Set
                .build();

        return ApiResponseGenerator.success(res, HttpStatus.OK);
    }
}
