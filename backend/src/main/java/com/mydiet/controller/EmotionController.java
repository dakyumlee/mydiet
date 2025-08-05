@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionService;

    @PostMapping
    public ResponseEntity<?> saveEmotion(@RequestBody EmotionRequest request) {
        EmotionLog saved = emotionService.saveEmotion(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayEmotions(@RequestParam Long userId) {
        return ResponseEntity.ok(emotionService.getTodayEmotions(userId));
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> getEmotionAnalytics(@RequestParam Long userId,
                                               @RequestParam(required = false) Integer days) {
        return ResponseEntity.ok(emotionService.getEmotionAnalytics(userId, days != null ? days : 7));
    }

    @DeleteMapping("/{emotionId}")
    public ResponseEntity<?> deleteEmotion(@PathVariable Long emotionId,
                                         @RequestParam Long userId) {
        emotionService.deleteEmotion(emotionId, userId);
        return ResponseEntity.ok().build();
    }
}