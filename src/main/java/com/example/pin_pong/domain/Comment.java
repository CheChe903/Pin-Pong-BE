package com.example.pin_pong.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commentId")
    private Long id;

    @Column(name = "selected")
    private Boolean selected;

    @Column(name = "content")
    private String content;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private Post post;

    @Builder
    public Comment(Boolean selected, String content, Member member, Post post) {
        this.selected = selected;
        this.content = content;
        this.member = member;
        this.post = post;
    }
}
