package com.example.pin_pong.service;

import com.example.pin_pong.domain.Member;
import com.example.pin_pong.domain.Post;
import com.example.pin_pong.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));
    }

    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }

    public Post addLike(Long postId, Member member) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        post.getLikedMembers().add(member);
        return postRepository.save(post);
    }

    public int getLikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        return post.getLikedMembers().size();
    }

    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> findByAuthorGithubId(String githubId) {
        return postRepository.findByAuthorGithubId(githubId);
    }

    // Add other methods as needed for more complex business logic
}
