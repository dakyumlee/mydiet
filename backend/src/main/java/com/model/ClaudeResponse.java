@Entity
public class ClaudeResponse {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    private String type; // insult, praise, motivation, etc
    private String content;

    private LocalDateTime createdAt;
}
