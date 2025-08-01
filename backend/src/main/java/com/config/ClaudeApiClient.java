@Component
@RequiredArgsConstructor
public class ClaudeApiClient {

    @Value("${claude.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String askClaude(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = Map.of(
            "model", "claude-3-opus-20240229",
            "messages", List.of(Map.of("role", "user", "content", prompt)),
            "temperature", 0.8
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "https://api.anthropic.com/v1/messages",
            entity,
            Map.class
        );

        try {
            List<Map<String, Object>> content = (List<Map<String, Object>>) ((Map) response.getBody().get("content"));
            return (String) content.get(0).get("text");
        } catch (Exception e) {
            return "Claude 응답 실패: " + e.getMessage();
        }
    }
}
