@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
public class EmotionController {
    
    private final EmotionService emotionService;
    
    @PostMapping
    public ResponseEntity<?> saveEmotion(@RequestBody EmotionRequest request, HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        if (userId == null) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "로그인이 필요합니다"));
        }
        
        request.setUserId(userId);
        
        try {
            EmotionLog saved = emotionService.saveEmotion(request);
            return ResponseEntity.ok(new ApiResponse(true, "감정 기록 성공", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "감정 기록 실패: " + e.getMessage()));
        }
    }
    
    @GetMapping("/today")
    public ResponseEntity<?> getTodayEmotions(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "로그인이 필요합니다"));
        }
        
        try {
            List<EmotionLog> emotions = emotionService.getTodayEmotions(userId);
            return ResponseEntity.ok(new ApiResponse(true, "오늘 감정 조회 성공", emotions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "감정 조회 실패: " + e.getMessage()));
        }
    }
    
    private Long getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (Long) session.getAttribute("userId");
    }
}