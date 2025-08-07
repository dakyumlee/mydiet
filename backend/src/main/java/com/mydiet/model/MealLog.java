package com.mydiet.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "meal_logs")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealLog {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String description;
    
    private String photoUrl; // optional
    
    private Integer caloriesEstimate;

    @Column(nullable = false)
    private LocalDate date;

    @PrePersist
    public void prePersist() {
        if (date == null) {
            date = LocalDate.now();
        }
    }
}