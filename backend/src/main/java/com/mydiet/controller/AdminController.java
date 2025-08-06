package com.mydiet.controller;

import com.mydiet.model.User;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserRepository userRepository;

    /**
     * 관리자 로그인 처리
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> adminLogin(@RequestBody Map<String, Object> request, HttpSession session) {
        log.info("=== 관리자 로그인 시도 ===");
        
        try {
            String email = (String) request.get("email");
            String password = (String) request.get("password");
            
            log.info("로그인 시도 - 이메일: {}", email);
            
            // 관리자 계정 확인 (사용자 지정 계정)
            if ("oicrcutie".equals(email) && "aa667788".equals(password)) {
                // 관리자 사용자 생성 또는 조회 (이메일이 아닌 아이디로 로그인하지만 이메일 형태로 저장)
                String adminEmail = "oicrcutie@mydiet.com"; // 실제 저장될 이메일
                User admin = userRepository.findByEmail(adminEmail).orElse(null);
                
                if (admin == null) {
                    admin = new User();
                    admin.setEmail(adminEmail);
                    admin.setNickname("oicrcutie (관리자)");
                    admin.setRole("ADMIN");
                    admin.setWeightGoal(70.0);
                    admin.setEmotionMode("무자비");
                    admin = userRepository.save(admin);
                    log.info("관리자 계정 생성 완료: ID={}, 닉네임={}", admin.getId(), admin.getNickname());
                }
                
                // 관리자 세션 설정
                session.setAttribute("userId", admin.getId());
                session.setAttribute("userRole", "ADMIN");
                session.setAttribute("isAdmin", true);
                
                log.info("관리자 로그인 성공: ID={}, 세션ID={}", admin.getId(), session.getId());
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "관리자 로그인 성공",
                    "user", Map.of(
                        "id", admin.getId(),
                        "nickname", admin.getNickname(),
                        "email", admin.getEmail(),
                        "role", admin.getRole()
                    ),
                    "redirectUrl", "/admin-dashboard.html"
                ));
            } else {
                log.warn("관리자 로그인 실패 - 잘못된 인증 정보: 시도한 ID={}", email);
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "잘못된 아이디 또는 비밀번호입니다"
                ));
            }
            
        } catch (Exception e) {
            log.error("관리자 로그인 처리 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "로그인 처리 중 오류가 발생했습니다"
            ));
        }
    }

    /**
     * 관리자 권한 확인
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAdminAccess(HttpSession session) {
        log.info("=== 관리자 권한 확인 ===");
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
            
            log.info("세션 확인 - userId: {}, isAdmin: {}", userId, isAdmin);
            
            if (userId == null || !Boolean.TRUE.equals(isAdmin)) {
                log.warn("관리자 권한 없음");
                return ResponseEntity.ok(Map.of(
                    "hasAccess", false,
                    "message", "관리자 권한이 필요합니다"
                ));
            }
            
            User user = userRepository.findById(userId).orElse(null);
            if (user == null || !"ADMIN".equals(user.getRole())) {
                log.warn("관리자 사용자 정보 없음 또는 권한 부족");
                return ResponseEntity.ok(Map.of(
                    "hasAccess", false,
                    "message", "관리자 권한이 확인되지 않습니다"
                ));
            }
            
            log.info("관리자 권한 확인 완료: {}", user.getNickname());
            return ResponseEntity.ok(Map.of(
                "hasAccess", true,
                "user", Map.of(
                    "id", user.getId(),
                    "nickname", user.getNickname(),
                    "email", user.getEmail(),
                    "role", user.getRole()
                )
            ));
            
        } catch (Exception e) {
            log.error("관리자 권한 확인 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "hasAccess", false,
                "message", "권한 확인 중 오류가 발생했습니다"
            ));
        }
    }

    /**
     * 모든 사용자 조회 (관리자용)
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers(HttpSession session) {
        log.info("=== 모든 사용자 조회 (관리자) ===");
        
        try {
            // 관리자 권한 확인
            Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
            if (!Boolean.TRUE.equals(isAdmin)) {
                return ResponseEntity.status(403).body(Map.of("error", "관리자 권한이 필요합니다"));
            }
            
            List<User> users = userRepository.findAll();
            log.info("전체 사용자 수: {}", users.size());
            
            return ResponseEntity.ok(Map.of(
                "users", users,
                "totalUsers", users.size()
            ));
            
        } catch (Exception e) {
            log.error("사용자 목록 조회 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 사용자 상세 정보 조회
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> getUserDetail(@PathVariable Long userId, HttpSession session) {
        log.info("=== 사용자 상세 정보 조회: userId={} ===", userId);
        
        try {
            // 관리자 권한 확인
            Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
            if (!Boolean.TRUE.equals(isAdmin)) {
                return ResponseEntity.status(403).body(Map.of("error", "관리자 권한이 필요합니다"));
            }
            
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "사용자를 찾을 수 없습니다"));
            }
            
            return ResponseEntity.ok(Map.of(
                "user", user,
                "isAdmin", "ADMIN".equals(user.getRole())
            ));
            
        } catch (Exception e) {
            log.error("사용자 상세 정보 조회 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 사용자 역할 변경 (관리자 승격/해제)
     */
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<Map<String, Object>> changeUserRole(@PathVariable Long userId, @RequestBody Map<String, Object> request, HttpSession session) {
        log.info("=== 사용자 역할 변경: userId={} ===", userId);
        
        try {
            // 관리자 권한 확인
            Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
            if (!Boolean.TRUE.equals(isAdmin)) {
                return ResponseEntity.status(403).body(Map.of("error", "관리자 권한이 필요합니다"));
            }
            
            String newRole = (String) request.get("role");
            if (!"USER".equals(newRole) && !"ADMIN".equals(newRole)) {
                return ResponseEntity.badRequest().body(Map.of("error", "유효하지 않은 역할입니다"));
            }
            
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "사용자를 찾을 수 없습니다"));
            }
            
            String oldRole = user.getRole();
            user.setRole(newRole);
            userRepository.save(user);
            
            log.info("사용자 역할 변경 완료: {} -> {}", oldRole, newRole);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "사용자 역할이 변경되었습니다",
                "user", user
            ));
            
        } catch (Exception e) {
            log.error("사용자 역할 변경 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 사용자 삭제
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long userId, HttpSession session) {
        log.info("=== 사용자 삭제: userId={} ===", userId);
        
        try {
            // 관리자 권한 확인
            Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
            if (!Boolean.TRUE.equals(isAdmin)) {
                return ResponseEntity.status(403).body(Map.of("error", "관리자 권한이 필요합니다"));
            }
            
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "사용자를 찾을 수 없습니다"));
            }
            
            String nickname = user.getNickname();
            userRepository.delete(user);
            
            log.info("사용자 삭제 완료: {}", nickname);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "사용자가 삭제되었습니다: " + nickname
            ));
            
        } catch (Exception e) {
            log.error("사용자 삭제 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> adminLogout(HttpSession session) {
        log.info("=== 관리자 로그아웃 ===");
        
        try {
            session.invalidate();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "로그아웃되었습니다",
                "redirectUrl", "/admin-login.html"
            ));
            
        } catch (Exception e) {
            log.error("로그아웃 처리 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "로그아웃 처리 중 오류가 발생했습니다"
            ));
        }
    }
}