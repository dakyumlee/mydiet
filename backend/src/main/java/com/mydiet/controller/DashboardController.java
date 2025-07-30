@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/today")
    public ResponseEntity<?> getTodayData(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        DashboardData data = dashboardService.getTodayData(userId);
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @PostMapping("/weight")
    public ResponseEntity<?> saveWeight(@RequestBody WeightRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        WeightLog saved = dashboardService.saveWeight(userId, request.getWeight(), request.getNote());
        return ResponseEntity.ok(Map.of("success", true, "data", saved));
    }

    @PostMapping("/diary")
    public ResponseEntity<?> saveDiary(@RequestBody DiaryRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        DiaryLog saved = dashboardService.saveDiary(userId, request.getTitle(), request.getContent());
        return ResponseEntity.ok(Map.of("success", true, "data", saved));
    }

    @PostMapping("/emotion")
    public ResponseEntity<?> saveEmotion(@RequestBody EmotionRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        EmotionLog saved = dashboardService.saveEmotion(userId, request.getMood(), request.getNote());
        return ResponseEntity.ok(Map.of("success", true, "data", saved));
    }
}

@Data
class WeightRequest {
    private Double weight;
    private String note;
}

@Data
class DiaryRequest {
    private String title;
    private String content;
}

@Data
class EmotionRequest {
    private String mood;
    private String note;
}