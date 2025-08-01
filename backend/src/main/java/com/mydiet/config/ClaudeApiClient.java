package com.mydiet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClaudeApiClient {

    @Value("${claude.api.key:dummy-key}")
    private String apiKey;

    public String askClaude(String prompt) {
        // 일단 더미 응답 (나중에 실제 API 연동)
        if (prompt.contains("무자비")) {
            return "뭐 이것밖에 안 먹었어? 이래서 살이 빠지겠냐? 🙄";
        } else if (prompt.contains("츤데레")) {
            return "별로 대단하지 않지만... 그래도 노력은 인정해줄게 😤";
        } else if (prompt.contains("다정함")) {
            return "오늘도 정말 수고했어요! 조금씩 발전하고 있는 게 보여요 💕";
        } else {
            return "오늘 하루도 화이팅! 🔥";
        }
    }
}
