package com.mydiet.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "claude_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClaudeResponse {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String type; // insult, praise, motivation, etc
    private String content;

    private LocalDateTime createdAt;
}