@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final AdminService adminService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(403)
                .body(new ApiResponse(false, "관리자 권한이 필요합니다"));
        }
        
        try {
            AdminSummaryResponse summary = adminService.getDashboardSummary();
            return ResponseEntity.ok(new ApiResponse(true, "대시보드 조회 성공", summary));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "대시보드 조회 실패: " + e.getMessage()));
        }
    }
    
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(403)
                .body(new ApiResponse(false, "관리자 권한이 필요합니다"));
        }
        
        try {
            List<UserResponse> users = adminService.getAllUsers();
            return ResponseEntity.ok(new ApiResponse(true, "사용자 목록 조회 성공", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "사용자 목록 조회 실패: " + e.getMessage()));
        }
    }
    
    @GetMapping("/meals")
    public ResponseEntity<?> getRecentMeals(HttpServletRequest request,
            @RequestParam(defaultValue = "50") int limit) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(403)
                .body(new ApiResponse(false, "관리자 권한이 필요합니다"));
        }
        
        try {
            List<MealLogResponse> meals = adminService.getRecentMeals(limit);
            return ResponseEntity.ok(new ApiResponse(true, "식단 기록 조회 성공", meals));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "식단 기록 조회 실패: " + e.getMessage()));
        }
    }
    
    @GetMapping("/claude-responses")
    public ResponseEntity<?> getClaudeResponses(HttpServletRequest request,
            @RequestParam(defaultValue = "50") int limit) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(403)
                .body(new ApiResponse(false, "관리자 권한이 필요합니다"));
        }
        
        try {
            List<ClaudeResponseDto> responses = adminService.getRecentClaudeResponses(limit);
            return ResponseEntity.ok(new ApiResponse(true, "Claude 응답 조회 성공", responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Claude 응답 조회 실패: " + e.getMessage()));
        }
    }
    
    @GetMapping("/active-users")
    public ResponseEntity<?> getActiveUsers(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(403)
                .body(new ApiResponse(false, "관리자 권한이 필요합니다"));
        }
        
        try {
            List<ActiveUserResponse> activeUsers = adminService.getActiveUsers();
            return ResponseEntity.ok(new ApiResponse(true, "활성 사용자 조회 성공", activeUsers));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "활성 사용자 조회 실패: " + e.getMessage()));
        }
    }
    
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        
        String role = (String) session.getAttribute("userRole");
        return "ADMIN".equals(role);
    }
}