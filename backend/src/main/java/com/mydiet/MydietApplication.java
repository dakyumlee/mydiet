@SpringBootApplication
@EnableJpaAuditing
public class MyDietApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyDietApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner initData(UserRepository userRepository, 
                                    PasswordEncoder passwordEncoder) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                if (userRepository.count() == 0) {
                    User admin = new User();
                    admin.setNickname("관리자");
                    admin.setEmail("admin@mydiet.com");
                    admin.setPassword(passwordEncoder.encode("admin123"));
                    admin.setRole(User.Role.ADMIN);
                    admin.setStartWeight(70.0);
                    admin.setCurrentWeight(70.0);
                    admin.setWeightGoal(65.0);
                    admin.setEmotionMode("다정함");
                    admin.setDietStartDate(LocalDate.now());
                    userRepository.save(admin);

                    User testUser = new User();
                    testUser.setNickname("테스트유저");
                    testUser.setEmail("test@mydiet.com");
                    testUser.setPassword(passwordEncoder.encode("test123"));
                    testUser.setRole(User.Role.USER);
                    testUser.setStartWeight(80.0);
                    testUser.setCurrentWeight(78.5);
                    testUser.setWeightGoal(70.0);
                    testUser.setEmotionMode("무자비");
                    testUser.setDietStartDate(LocalDate.now().minusDays(30));
                    userRepository.save(testUser);
                    
                    System.out.println("=== 기본 계정 생성 완료 ===");
                    System.out.println("관리자: admin@mydiet.com / admin123");
                    System.out.println("사용자: test@mydiet.com / test123");
                }
            }
        };
    }
}