@Entity
@Table(name = "MEAL_LOG")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meal_seq")
    @SequenceGenerator(name = "meal_seq", sequenceName = "MEAL_LOG_SEQ", allocationSize = 1)
    @Column(name = "MEAL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(name = "FK_MEAL_USER"))
    private User user;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "PHOTO_URL", length = 500)
    private String photoUrl;

    @Column(name = "CALORIES_ESTIMATE")
    private Integer caloriesEstimate;

    @Column(name = "MEAL_DATE")
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