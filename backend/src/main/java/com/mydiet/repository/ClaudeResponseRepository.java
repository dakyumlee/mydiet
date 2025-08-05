package com.mydiet.repository;

import com.mydiet.model.ClaudeResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClaudeResponseRepository extends JpaRepository<ClaudeResponse, Long> {
    List<ClaudeResponse> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<ClaudeResponse> findAllByOrderByCreatedAtDesc();
    long countByUserId(Long userId);
    boolean existsByUserIdAndCreatedAtAfter(Long userId, LocalDateTime date);
}
