package com.example.pin_pong.repository;

import com.example.pin_pong.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findById(Long id); // 추가된 findById 메서드

    Optional<Member> findByGithubId(String githubId);
}
