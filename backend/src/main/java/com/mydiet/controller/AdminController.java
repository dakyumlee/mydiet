package com.mydiet.controller;

import com.mydiet.model.User;
import com.mydiet.repository.UserRepository;
import com.mydiet.service.AdminService;
import com.mydiet.service.OAuth2UserPrincipal;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> adminLogin(
        @RequestBody Map<String, String> request, 
        HttpSession session) {
        
        String email = request.get("email");
        String password = request.get("password");
        
        log.info("=== 관리자 로그인 시도: {} ===", email);
        
        if ("admin@mydiet.com".equals(email) && "admin123".equals(password)) {
            User admin = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newAdmin = User.builder()
                        .email(email)
                        .nickname("관리자")
                        .role("ADMIN")
                        .weightGoal(70.0)
                        .emotionMode("무자비")
                        .createdAt(LocalDateTime.now())
                        .build();
                    return userRepository.save(newAdmin);
                });
            
            session.setAttribute("adminId", admin.getId());
            session.setAttribute("adminEmail", admin.getEmail());
            session.setAttribute("adminNickname", admin.getNickname());
            session.setAttribute("adminRole", admin.getRole());
            session.setAttribute("isAdmin", true);
            session.setAttribute("authenticated", true);
            
            session.setAttribute("userId", admin.getId());
            session.setAttribute("userEmail", admin.getEmail());
            session.setAttribute("userNickname", admin.getNickname());
            session.setAttribute("userRole", admin.getRole());
            
            log.info("✅ 관리자 로그인 성공: {}", email);
            log.info("✅ 세션 ID: {}", session.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "관리자 로그인 성공",
                "adminId", admin.getId(),
                "adminEmail", admin.getEmail(),
                "adminNickname", admin.getNickname(),
                "redirectUrl", "/admin-dashboard.html"
            ));
        } else {
            log.warn("❌ 관리자 로그인 실패: {}", email);
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "관리자 인증 실패"));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers(HttpSession session) {
        if (!isAdminSession(session)) {
            return ResponseEntity.status(403)
                .body(Map.of("error", "관리자 권한이 필요합니다."));
        }

        try {
            List<User> users = userRepository.findAll();
            log.info("=== 관리자 - 전체 사용자 조회: {}명 ===", users.size());
            
            Map<String, Object> response = new HashMap<>();
            response.put("users", users);
            response.put("totalCount", users.size());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("사용자 목록 조회 실패", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "사용자 목록 조회 실패"));
        }
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> getUserDetail(
        @PathVariable Long userId, HttpSession session) {
        
        if (!isAdminSession(session)) {
            return ResponseEntity.status(403)
                .body(Map.of("error", "관리자 권한이 필요합니다."));
        }

        log.info("=== 사용자 상세 정보 조회: userId={} ===", userId);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        
        Map<String, Object> userDetail = new HashMap<>();
        userDetail.put("user", user);
        userDetail.put("lastLoginDate", "구현 예정");
        userDetail.put("totalMeals", "구현 예정");
        userDetail.put("totalWorkouts", "구현 예정");
        userDetail.put("moodAnalysis", "구현 예정");
        
        return ResponseEntity.ok(userDetail);
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<Map<String, Object>> updateUserRole(
        @PathVariable Long userId,
        @RequestBody Map<String, String> request,
        HttpSession session) {
        
        if (!isAdminSession(session)) {
            return ResponseEntity.status(403)
                .body(Map.of("error", "관리자 권한이 필요합니다."));
        }

        String newRole = request.get("role");
        if (!"USER".equals(newRole) && !"ADMIN".equals(newRole)) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "잘못된 역할입니다. USER 또는 ADMIN만 가능합니다."));
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        String oldRole = user.getRole();
        user.setRole(newRole);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("사용자 역할 변경: userId={}, {} -> {}", userId, oldRole, newRole);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", String.format("사용자 역할이 %s에서 %s로 변경되었습니다.", oldRole, newRole),
            "user", user
        ));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(
        @PathVariable Long userId, HttpSession session) {
        
        if (!isAdminSession(session)) {
            return ResponseEntity.status(403)
                .body(Map.of("error", "관리자 권한이 필요합니다."));
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        
        // 관리자 계정 삭제 방지
        if ("ADMIN".equals(user.getRole())) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "관리자 계정은 삭제할 수 없습니다."));
        }

        try {
            userRepository.deleteById(userId);
            log.info("사용자 삭제 완료: userId={}, email={}", userId, user.getEmail());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", String.format("사용자 '%s'가 삭제되었습니다.", user.getNickname())
            ));
        } catch (Exception e) {
            log.error("사용자 삭제 실패: userId={}", userId, e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "사용자 삭제 중 오류가 발생했습니다."));
        }
    }


    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAdminStats(HttpSession session) {
        if (!isAdminSession(session)) {
            return ResponseEntity.status(403)
                .body(Map.of("error", "관리자 권한이 필요합니다."));
        }

        try {
            long totalUsers = userRepository.count();
            long adminCount = userRepository.countByRole("ADMIN");
            long userCount = userRepository.countByRole("USER");
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", totalUsers);
            stats.put("adminCount", adminCount);
            stats.put("userCount", userCount);
            stats.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("관리자 통계 조회 실패", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "통계 조회 실패"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> adminLogout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "관리자 로그아웃 완료"
        ));
    }

    private boolean isAdminSession(HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        Long adminId = (Long) session.getAttribute("adminId");
        return Boolean.TRUE.equals(isAdmin) && adminId != null;
    }
}