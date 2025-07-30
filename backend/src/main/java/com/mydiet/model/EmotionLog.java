package com.mydiet.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "emotion_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmotionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private String emotion;
    private Integer stressLevel;
    private String note;
    private LocalDate date;
    private LocalDateTime createdAt;
}