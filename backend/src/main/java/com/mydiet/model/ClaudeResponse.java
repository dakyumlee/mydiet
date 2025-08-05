package com.mydiet.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "CLAUDE_RESPONSES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaudeResponse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "claude_seq")
    @SequenceGenerator(name = "claude_seq", sequenceName = "CLAUDE_SEQ", allocationSize = 1)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(length = 50)
    private String type;
    
    @Column(length = 2000)
    private String content;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}