@Entity
@Table(name = "CLAUDE_RESPONSE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaudeResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "claude_seq")
    @SequenceGenerator(name = "claude_seq", sequenceName = "CLAUDE_RESPONSE_SEQ", allocationSize = 1)
    @Column(name = "RESPONSE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(name = "FK_CLAUDE_USER"))
    private User user;

    @Column(name = "RESPONSE_TYPE", length = 50)
    private String type;

    @Column(name = "CONTENT", length = 4000)
    private String content;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}