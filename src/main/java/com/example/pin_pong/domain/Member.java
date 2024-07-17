package com.example.pin_pong.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="memberId")
    private Long id;

    @Column(name="githubId")
    private String githubId;

    @Column(name="githubImage")
    private String githubImage;

    @Column(name="pin")
    private int pin;

    @Column(name="lastLogin")
    private LocalDateTime lastLogin;

    @ManyToMany
    @JoinTable(
            name = "member_tech_stacks",
            joinColumns = @JoinColumn(name = "memberId"),
            inverseJoinColumns = @JoinColumn(name = "techStackId")
    )
    private Set<TechStack> techStacks = new HashSet<>();


    @Builder
    public Member(String githubId, String githubImage, Set<TechStack> techStacks, Integer pin, LocalDateTime lastLogin) {
        this.githubId = githubId;
        this.githubImage = githubImage;
        this.techStacks = techStacks;
        this.pin = pin;
        this.lastLogin = lastLogin;
    }
}