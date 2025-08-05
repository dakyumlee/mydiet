package com.mydiet.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "EMOTION_LOGS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmotionLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "emotion_seq")
    @SequenceGenerator(name = "emotion_seq", sequenceName = "EMOTION_SEQ", allocationSize = 1)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(length = 50)
    private String mood;
    
    @Column(length = 1000)
    private String note;
    
    @Column(name = "emotion_date")
    private LocalDate date;
    
    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = LocalDate.now();
        }
    }
}