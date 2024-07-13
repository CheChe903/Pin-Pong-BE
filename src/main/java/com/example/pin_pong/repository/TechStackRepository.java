package com.example.pin_pong.repository;

import com.example.pin_pong.domain.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechStackRepository extends JpaRepository<TechStack, Long> {
    Optional<TechStack> findByTechName(String techName);
}
