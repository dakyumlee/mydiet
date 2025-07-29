@Entity
public class WorkoutLog {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    private String type; // 걷기, 뛰기 등
    private Integer duration; // 분 단위
    private Integer caloriesBurned;

    private LocalDate date;
}
