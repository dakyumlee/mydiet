package com.mydiet.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "emotion_logs")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmotionLog {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String mood; // ex: 우울, 짜증, 행복, 분노
    
    private String note;

    @Column(nullable = false)
    private LocalDate date;

    @PrePersist
    public void prePersist() {
        if (date == null) {
            date = LocalDate.now();
        }
    }
}