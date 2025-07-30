@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(HttpSession session,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "관리자 권한이 필요합니다."));
        }

        Page<UserSummary> users = adminService.getAllUsers(page, size);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", users.getContent(),
            "totalPages", users.getTotalPages(),
            "totalElements", users.getTotalElements()
        ));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserDetail(@PathVariable Long userId, HttpSession session) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "관리자 권한이 필요합니다."));
        }

        UserDetail detail = adminService.getUserDetail(userId);
        return ResponseEntity.ok(Map.of("success", true, "data", detail));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId, HttpSession session) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "관리자 권한이 필요합니다."));
        }

        adminService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("success", true, "message", "사용자가 삭제되었습니다."));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(HttpSession session) {
        String userRole = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "관리자 권한이 필요합니다."));
        }

        AdminStats stats = adminService.getStats();
        return ResponseEntity.ok(Map.of("success", true, "data", stats));
    }
}