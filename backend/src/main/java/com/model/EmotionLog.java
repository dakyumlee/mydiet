@Entity
public class EmotionLog {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    private String mood; // ex: 우울, 짜증, 행복, 분노
    private String note;

    private LocalDate date;
}
