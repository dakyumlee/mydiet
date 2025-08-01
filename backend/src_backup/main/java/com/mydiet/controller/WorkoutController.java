@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
public class WorkoutController {
    
    private final WorkoutService workoutService;
    
    @PostMapping
    public ResponseEntity<?> saveWorkout(@RequestBody WorkoutRequest request, HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        if (userId == null) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "로그인이 필요합니다"));
        }
        
        request.setUserId(userId);
        
        try {
            WorkoutLog saved = workoutService.saveWorkout(request);
            return ResponseEntity.ok(new ApiResponse(true, "운동 기록 성공", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "운동 기록 실패: " + e.getMessage()));
        }
    }
    
    @GetMapping("/today")
    public ResponseEntity<?> getTodayWorkouts(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "로그인이 필요합니다"));
        }
        
        try {
            List<WorkoutLog> workouts = workoutService.getTodayWorkouts(userId);
            return ResponseEntity.ok(new ApiResponse(true, "오늘 운동 조회 성공", workouts));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "운동 조회 실패: " + e.getMessage()));
        }
    }
    
    private Long getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (Long) session.getAttribute("userId");
    }
}