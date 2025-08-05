@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping
    public ResponseEntity<?> saveWorkout(@RequestBody WorkoutRequest request) {
        WorkoutLog saved = workoutService.saveWorkout(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayWorkouts(@RequestParam Long userId) {
        return ResponseEntity.ok(workoutService.getTodayWorkouts(userId));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getWorkoutStats(@RequestParam Long userId, 
                                           @RequestParam(required = false) String period) {
        return ResponseEntity.ok(workoutService.getWorkoutStats(userId, period));
    }

    @DeleteMapping("/{workoutId}")
    public ResponseEntity<?> deleteWorkout(@PathVariable Long workoutId, 
                                         @RequestParam Long userId) {
        workoutService.deleteWorkout(workoutId, userId);
        return ResponseEntity.ok().build();
    }
}