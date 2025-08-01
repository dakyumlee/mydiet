@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
            
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "비밀번호가 일치하지 않습니다"));
            }
            
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userRole", user.getRole());
            
            return ResponseEntity.ok(new ApiResponse(true, "로그인 성공", 
                UserResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .role(user.getRole())
                    .build()));
                    
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "로그인 실패: " + e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            if (userService.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "이미 존재하는 이메일입니다"));
            }
            
            User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .weightGoal(request.getWeightGoal())
                .emotionMode(request.getEmotionMode())
                .role("USER")
                .createdAt(LocalDateTime.now())
                .build();
                
            User savedUser = userService.save(user);
            
            return ResponseEntity.ok(new ApiResponse(true, "회원가입 성공", 
                UserResponse.from(savedUser)));
                
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "회원가입 실패: " + e.getMessage()));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(new ApiResponse(true, "로그아웃 성공"));
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(401)
                .body(new ApiResponse(false, "인증되지 않은 사용자"));
        }
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new ApiResponse(false, "세션이 만료되었습니다"));
        }
        
        User user = userService.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
            
        return ResponseEntity.ok(new ApiResponse(true, "사용자 정보 조회 성공", 
            UserResponse.from(user)));
    }
}