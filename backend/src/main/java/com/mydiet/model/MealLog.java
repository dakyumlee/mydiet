package com.mydiet.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "MEAL_LOGS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meal_seq")
    @SequenceGenerator(name = "meal_seq", sequenceName = "MEAL_SEQ", allocationSize = 1)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "photo_url", length = 500)
    private String photoUrl;
    
    @Column(name = "calories_estimate")
    private Integer caloriesEstimate;
    
    @Column(name = "meal_date")
    private LocalDate date;
    
    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = LocalDate.now();
        }
    }
}