package com.mydiet.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "users")
public class User {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nickname;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    private Double currentWeight;
    private Double weightGoal;
    private Double startWeight;
    
    private String emotionMode;
    
    @Column(name = "diet_start_date", nullable = false)
    private LocalDate dietStartDate; // 다이어트 시작일 (고정!)
    
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (dietStartDate == null) {
            dietStartDate = LocalDate.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public int getDietDays() {
        if (dietStartDate == null) return 0;
        return (int) ChronoUnit.DAYS.between(dietStartDate, LocalDate.now()) + 1;
    }
    
    public enum Role {
        USER, ADMIN
    }

    public User() {}
    
    public User(String nickname, String email, String password, Double startWeight, Double weightGoal, String emotionMode) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.startWeight = startWeight;
        this.currentWeight = startWeight;
        this.weightGoal = weightGoal;
        this.emotionMode = emotionMode;
        this.dietStartDate = LocalDate.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Double getCurrentWeight() { return currentWeight; }
    public void setCurrentWeight(Double currentWeight) { this.currentWeight = currentWeight; }
    
    public Double getWeightGoal() { return weightGoal; }
    public void setWeightGoal(Double weightGoal) { this.weightGoal = weightGoal; }
    
    public Double getStartWeight() { return startWeight; }
    public void setStartWeight(Double startWeight) { this.startWeight = startWeight; }
    
    public String getEmotionMode() { return emotionMode; }
    public void setEmotionMode(String emotionMode) { this.emotionMode = emotionMode; }
    
    public LocalDate getDietStartDate() { return dietStartDate; }
    public void setDietStartDate(LocalDate dietStartDate) { this.dietStartDate = dietStartDate; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}