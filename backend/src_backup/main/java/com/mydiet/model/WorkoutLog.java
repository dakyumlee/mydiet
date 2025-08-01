@Entity
@Table(name = "WORKOUT_LOG")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workout_seq")
    @SequenceGenerator(name = "workout_seq", sequenceName = "WORKOUT_LOG_SEQ", allocationSize = 1)
    @Column(name = "WORKOUT_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(name = "FK_WORKOUT_USER"))
    private User user;

    @Column(name = "WORKOUT_TYPE", length = 50)
    private String type;

    @Column(name = "DURATION_MINUTES")
    private Integer duration;

    @Column(name = "CALORIES_BURNED")
    private Integer caloriesBurned;

    @Column(name = "WORKOUT_DATE")
    private LocalDate date;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (date == null) {
            date = LocalDate.now();
        }
    }
}
