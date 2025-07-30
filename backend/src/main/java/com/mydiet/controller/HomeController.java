@Controller
public class HomeController {
    @GetMapping("/")
    public String redirectToIndex() {
        return "forward:/index.html";
    }
}
