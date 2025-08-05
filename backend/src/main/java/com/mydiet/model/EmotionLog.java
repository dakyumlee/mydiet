package com.mydiet.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "emotion_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmotionLog {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String mood; // ex: 우울, 짜증, 행복, 분노
    private String note;

    private LocalDate date;
}