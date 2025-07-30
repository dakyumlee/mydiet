@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        
        User user = new User();
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStartWeight(request.getStartWeight());
        user.setCurrentWeight(request.getStartWeight());
        user.setWeightGoal(request.getWeightGoal());
        user.setEmotionMode(request.getEmotionMode());
        user.setDietStartDate(LocalDate.now());
        
        return userRepository.save(user);
    }
    
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
            
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        
        return user;
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }
}