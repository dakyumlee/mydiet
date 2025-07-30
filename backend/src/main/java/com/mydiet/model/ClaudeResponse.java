package com.mydiet.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "claude_responses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaudeResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private String question;
    private String response;
    private LocalDateTime createdAt;
}