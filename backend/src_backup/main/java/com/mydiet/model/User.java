@Entity
@Table(name = "MYDIET_USER")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 1)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "NICKNAME", length = 50)
    private String nickname;

    @Column(name = "EMAIL", length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "PASSWORD", length = 255, nullable = false)
    private String password;

    @Column(name = "USER_ROLE", length = 20)
    private String role = "USER";

    @Column(name = "WEIGHT_GOAL", precision = 5, scale = 2)
    private Double weightGoal;

    @Column(name = "TARGET_CALORIES", precision = 6, scale = 2)
    private Double targetCalories;

    @Column(name = "EMOTION_MODE", length = 20)
    private String emotionMode;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
