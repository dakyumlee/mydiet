package com.mydiet.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "workout_logs")
@Data
public class WorkoutLog {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String type;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDate date;
}