@Entity
public class MealLog {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    private String description;
    private String photoUrl; // optional
    private Integer caloriesEstimate;

    private LocalDate date;
}
