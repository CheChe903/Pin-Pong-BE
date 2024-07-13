package com.example.pin_pong.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class TechStack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="techStackId")
    private Long id;

    @Column(name="techName")
    private String techName;


    @Builder
    public TechStack(String techName) {
        this.techName = techName;
    }
}