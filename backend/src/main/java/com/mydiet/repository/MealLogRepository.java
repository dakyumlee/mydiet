package com.mydiet.repository;

import com.mydiet.model.MealLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealLogRepository extends JpaRepository<MealLog, Long> {
    
    List<MealLog> findByUserIdAndDate(Long userId, LocalDate date);
    
    List<MealLog> findByUserIdOrderByDateDesc(Long userId); 
    
    @Query("SELECT COUNT(m) FROM MealLog m WHERE m.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}