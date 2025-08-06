package com.mydiet.repository;

import com.mydiet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findBySocialId(String socialId);
    long countByLastLoginAtAfter(LocalDateTime dateTime);
}