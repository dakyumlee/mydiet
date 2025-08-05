package com.mydiet.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "WORKOUT_LOGS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workout_seq")
    @SequenceGenerator(name = "workout_seq", sequenceName = "WORKOUT_SEQ", allocationSize = 1)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(length = 100)
    private String type;
    
    private Integer duration;
    
    @Column(name = "calories_burned")
    private Integer caloriesBurned;
    
    @Column(name = "workout_date")
    private LocalDate date;
    
    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = LocalDate.now();
        }
    }
}