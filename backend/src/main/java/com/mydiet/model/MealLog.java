package com.mydiet.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private String mealType;
    private String foodName;
    private Double calories;
    private Double protein;
    private Double carbs;
    private Double fat;
    private LocalDate date;
    private LocalDateTime createdAt;
}