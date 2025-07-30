@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MealController {

    private final MealService mealService;

    @PostMapping
    public ResponseEntity<?> saveMeal(@RequestBody MealRequest request) {
        try {
            MealLog saved = mealService.saveMeal(request);
            return ResponseEntity.ok(Map.of("success", true, "data", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayMeals(@RequestParam Long userId) {
        try {
            List<MealLog> meals = mealService.getTodayMeals(userId);
            return ResponseEntity.ok(Map.of("success", true, "data", meals));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}

@Data
class MealRequest {
    private Long userId;
    private String description;
    private Integer caloriesEstimate;
    private String photoUrl;
}