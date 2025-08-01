@Entity
@Table(name = "EMOTION_LOG")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmotionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "emotion_seq")
    @SequenceGenerator(name = "emotion_seq", sequenceName = "EMOTION_LOG_SEQ", allocationSize = 1)
    @Column(name = "EMOTION_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(name = "FK_EMOTION_USER"))
    private User user;

    @Column(name = "MOOD", length = 50)
    private String mood;

    @Column(name = "NOTE", length = 1000)
    private String note;

    @Column(name = "EMOTION_DATE")
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