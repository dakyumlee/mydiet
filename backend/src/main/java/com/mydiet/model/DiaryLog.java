package com.mydiet.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "diary_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private String title;
    private String content;
    private String mood;
    private LocalDate date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}