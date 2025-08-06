package com.mydiet.repository;

import com.mydiet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 이메일로 사용자 찾기
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 역할별 사용자 개수 조회
     */
    long countByRole(String role);
    
    /**
     * 역할별 사용자 목록 조회
     */
    List<User> findByRole(String role);
    
    /**
     * 닉네임으로 사용자 찾기 (부분 일치)
     */
    List<User> findByNicknameContainingIgnoreCase(String nickname);
    
    /**
     * 특정 날짜 이후 가입한 사용자들
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * 목표 체중 범위로 사용자 찾기
     */
    @Query("SELECT u FROM User u WHERE u.weightGoal BETWEEN :minWeight AND :maxWeight")
    List<User> findByWeightGoalBetween(@Param("minWeight") Double minWeight, 
                                     @Param("maxWeight") Double maxWeight);
    
    /**
     * 감정 모드별 사용자 조회
     */
    List<User> findByEmotionMode(String emotionMode);
    
    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);
    
    /**
     * 최근 가입한 사용자들 (최신순)
     */
    List<User> findTop10ByOrderByCreatedAtDesc();
    
    /**
     * 활성 사용자 통계 (최근 30일 내 업데이트된 사용자)
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.updatedAt >= :thirtyDaysAgo")
    long countActiveUsers(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);
}