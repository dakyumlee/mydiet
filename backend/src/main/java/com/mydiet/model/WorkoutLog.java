package com.mydiet.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "workout_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    
    @Column(name = "calories_burned")
    private Integer caloriesBurned;

    @Column(nullable = false)
    private LocalDate date;
    
    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = LocalDate.now();
        }
    }
}