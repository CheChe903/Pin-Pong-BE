package com.example.pin_pong.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Getter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="postId")
    private Long id;

    @Column(name="postTitle")
    private String postTitle;

    @Column(name="prId")
    private Long prId;

    @ElementCollection
    @CollectionTable(name = "post_commit_map", joinColumns = @JoinColumn(name = "post_id"))
    @MapKeyColumn(name = "commit_id")
    @Column(name = "commit_content")
    private Map<String, String> commitList = new HashMap<>();

    @Column(name="content")
    private String content;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member author;

    @ManyToMany
    @JoinTable(
            name = "question_tech_stacks",
            joinColumns = @JoinColumn(name = "postId"),
            inverseJoinColumns = @JoinColumn(name = "techStackId")
    )
    private Set<TechStack> techStacks = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "post_likes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private Set<Member> likedMembers = new HashSet<>();

    @Builder
    public Post(String postTitle, Long prId, Map<String,String> commitList, String content, Member author, Set<TechStack> techStacks, Set<Member> likedMembers) {
        this.postTitle = postTitle;
        this.prId = prId;
        this.commitList = commitList;
        this.content = content;
        this.author = author;
        this.techStacks = techStacks;
        this.likedMembers = likedMembers;
    }
}
