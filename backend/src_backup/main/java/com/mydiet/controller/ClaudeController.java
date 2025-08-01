@RestController
@RequestMapping("/api/claude")
@RequiredArgsConstructor
public class ClaudeController {

    private final ClaudeService claudeService;

    @GetMapping("/message")
    public ResponseEntity<?> getClaudeMessage(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ApiResponse(false, "로그인이 필요합니다"));
        }
        
        try {
            String message = claudeService.generateResponse(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Claude 응답 성공", message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Claude 응답 실패: " + e.getMessage()));
        }
    }
    
    @GetMapping("/history")
    public ResponseEntity<?> getClaudeHistory(HttpServletRequest request,
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ApiResponse(false, "로그인이 필요합니다"));
        }
        
        try {
            List<ClaudeResponse> history = claudeService.getRecentResponses(userId, limit);
            return ResponseEntity.ok(new ApiResponse(true, "Claude 기록 조회 성공", history));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "기록 조회 실패: " + e.getMessage()));
        }
    }
    
    private Long getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (Long) session.getAttribute("userId");
    }
}