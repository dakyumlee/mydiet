package com.mydiet.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "workout_logs")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutLog {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String type; // 걷기, 뛰기 등
    
    private Integer duration; // 분 단위
    
    private Integer caloriesBurned;

    @Column(nullable = false)
    private LocalDate date;

    @PrePersist
    public void prePersist() {
        if (date == null) {
            date = LocalDate.now();
        }
    }
}