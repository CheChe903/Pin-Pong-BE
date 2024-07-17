package com.example.pin_pong.service;

import com.example.pin_pong.domain.Comment;
import com.example.pin_pong.domain.Member;
import com.example.pin_pong.repository.CommentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@Transactional
public class CommentService {

    private final MemberService memberService;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(MemberService memberService, CommentRepository commentRepository) {
        this.memberService = memberService;
        this.commentRepository = commentRepository;
    }

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + id));
    }

    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public Comment selectComment(Long commentId, Member member) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

        if (!comment.getMember().equals(member)) {
            throw new IllegalArgumentException("You do not have permission to select this comment.");
        }

        if (!comment.getSelected()) {
            comment.setSelected(true);
            memberService.increasePin(member.getId());
            return commentRepository.save(comment);
        } else {
            throw new IllegalArgumentException("Comment is already selected.");
        }
    }

    public Member findMemberByCommentId(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));
        return comment.getMember();
    }

    // Add other methods as needed for more complex business logic
}
