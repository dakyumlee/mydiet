package com.mydiet.controller;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {
    
    private final ClaudeApiClient claudeApiClient;
    
    @PostMapping("/question")
    public ResponseEntity<String> askQuestion(@RequestBody AIQuestionRequest request) {
        String prompt = "사용자 질문: " + request.getQuestion() + 
                       "\n\n건강, 다이어트, 운동에 대한 전문적이고 도움이 되는 답변을 해주세요.";
        
        String response = claudeApiClient.askClaude(prompt);
        return ResponseEntity.ok(response);
    }
}
