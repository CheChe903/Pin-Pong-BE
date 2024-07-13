package com.example.pin_pong.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="memberId")
    private Long id;

    @Column(name="githubId")
    private String githubid;

    @Column(name="githubImage")
    private String githubImage;

    @Column(name="pin")
    private Integer pin;

    @ManyToMany
    @JoinTable(
            name = "member_tech_stacks",
            joinColumns = @JoinColumn(name = "memberId"),
            inverseJoinColumns = @JoinColumn(name = "techStackId")
    )
    private Set<TechStack> techStacks = new HashSet<>();


    @Builder
    public Member(String githubid, String githubImage, Set<TechStack> techStacks, Integer pin) {
        this.githubid = githubid;
        this.githubImage = githubImage;
        this.techStacks = techStacks;
        this.pin = pin;
    }
}