package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mydiet.model.ClaudeResponse;

import java.util.List;

public interface ClaudeResponseRepository extends JpaRepository<ClaudeResponse, Long> {
    List<ClaudeResponse> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<ClaudeResponse> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
    List<ClaudeResponse> findTop10ByOrderByCreatedAtDesc();
    long countByUserId(Long userId);
}

