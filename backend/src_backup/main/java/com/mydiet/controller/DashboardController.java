@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping("/today")
    public ResponseEntity<?> getTodayStats(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ApiResponse(false, "인증이 필요합니다"));
        }
        
        try {
            TodayStatsResponse stats = dashboardService.getTodayStats(userId);
            return ResponseEntity.ok(new ApiResponse(true, "오늘 통계 조회 성공", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "통계 조회 실패: " + e.getMessage()));
        }
    }
    
    @GetMapping("/weekly")
    public ResponseEntity<?> getWeeklyStats(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ApiResponse(false, "인증이 필요합니다"));
        }
        
        try {
            StatisticsResponse stats = dashboardService.getWeeklyStats(userId);
            return ResponseEntity.ok(new ApiResponse(true, "주간 통계 조회 성공", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "통계 조회 실패: " + e.getMessage()));
        }
    }
    
    @GetMapping("/activities")
    public ResponseEntity<?> getRecentActivities(HttpServletRequest request,
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ApiResponse(false, "인증이 필요합니다"));
        }
        
        try {
            List<ActivityResponse> activities = dashboardService.getRecentActivities(userId, limit);
            return ResponseEntity.ok(new ApiResponse(true, "최근 활동 조회 성공", activities));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "활동 조회 실패: " + e.getMessage()));
        }
    }
    
    private Long getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (Long) session.getAttribute("userId");
    }
}