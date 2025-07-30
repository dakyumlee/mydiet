package com.mydiet.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "workout_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private String exerciseType;
    private Integer duration;
    private Double caloriesBurned;
    private String intensity;
    private LocalDate date;
    private LocalDateTime createdAt;
}