package com.mydiet.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String email;
    private String password;
    private String name;
    private Integer age;
    private Integer height;
    private Double currentWeight;
    private Double targetWeight;
    private String gender;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}