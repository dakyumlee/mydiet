@Entity
public class User {
    @Id @GeneratedValue
    private Long id;

    private String nickname;
    private String email;

    private Double weightGoal;
    private String emotionMode; // 예: 무자비, 츤데레, 다정함

    private LocalDateTime createdAt;
}
