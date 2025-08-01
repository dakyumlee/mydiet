package com.mydiet.repository;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mydiet.model.ClaudeResponse;

import java.util.List;

public interface ClaudeResponseRepository extends JpaRepository<ClaudeResponse, Long> {
    List<ClaudeResponse> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<ClaudeResponse> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
