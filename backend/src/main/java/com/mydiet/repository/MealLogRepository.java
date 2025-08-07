package com.mydiet.repository;

import com.mydiet.model.MealLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MealLogRepository extends JpaRepository<MealLog, Long> {
    List<MealLog> findByUserIdAndDate(Long userId, LocalDate date);
    
    @Query("SELECT COUNT(m) FROM MealLog m WHERE m.date = :date")
    long countByDate(@Param("date") LocalDate date);
    
    List<MealLog> findByUserId(Long userId);
}