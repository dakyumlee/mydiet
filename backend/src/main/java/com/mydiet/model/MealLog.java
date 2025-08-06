package com.mydiet.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "meal_logs")
@Data
public class MealLog {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String photoUrl; // base64 이미지 저장용
    
    private Integer caloriesEstimate;
    private LocalDate date;
}