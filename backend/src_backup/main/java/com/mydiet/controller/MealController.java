@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
public class MealController {
    
    private final MealService mealService;
    
    @PostMapping
    public ResponseEntity<?> saveMeal(@RequestBody MealRequest request, HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        if (userId == null) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "로그인이 필요합니다"));
        }
        
        request.setUserId(userId);
        
        try {
            MealLog saved = mealService.saveMeal(request);
            return ResponseEntity.ok(new ApiResponse(true, "식단 기록 성공", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "식단 기록 실패: " + e.getMessage()));
        }
    }
    
    @GetMapping("/today")
    public ResponseEntity<?> getTodayMeals(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "로그인이 필요합니다"));
        }
        
        try {
            List<MealLog> meals = mealService.getTodayMeals(userId);
            return ResponseEntity.ok(new ApiResponse(true, "오늘 식단 조회 성공", meals));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "식단 조회 실패: " + e.getMessage()));
        }
    }
    
    @GetMapping("/range")
    public ResponseEntity<?> getMealsByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "로그인이 필요합니다"));
        }
        
        try {
            List<MealLog> meals = mealService.getMealsByDateRange(userId, startDate, endDate);
            return ResponseEntity.ok(new ApiResponse(true, "기간별 식단 조회 성공", meals));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "식단 조회 실패: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{mealId}")
    public ResponseEntity<?> deleteMeal(@PathVariable Long mealId, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "로그인이 필요합니다"));
        }
        
        try {
            mealService.deleteMeal(mealId, userId);
            return ResponseEntity.ok(new ApiResponse(true, "식단 기록 삭제 성공"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "식단 삭제 실패: " + e.getMessage()));
        }
    }
    
    private Long getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (Long) session.getAttribute("userId");
    }
}
