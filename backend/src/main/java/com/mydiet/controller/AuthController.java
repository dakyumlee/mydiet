@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "회원가입이 완료되었습니다.",
                "userId", user.getId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            User user = authService.login(request.getEmail(), request.getPassword());
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole().name());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "로그인 성공",
                "user", Map.of(
                    "id", user.getId(),
                    "nickname", user.getNickname(),
                    "role", user.getRole().name()
                )
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "로그아웃 완료"
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "로그인이 필요합니다."
            ));
        }
        
        User user = authService.findById(userId);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "user", Map.of(
                "id", user.getId(),
                "nickname", user.getNickname(),
                "email", user.getEmail(),
                "role", user.getRole().name(),
                "dietDays", user.getDietDays()
            )
        ));
    }
}

@Data
class RegisterRequest {
    private String nickname;
    private String email;
    private String password;
    private Double startWeight;
    private Double weightGoal;
    private String emotionMode;
}

@Data
class LoginRequest {
    private String email;
    private String password;
}