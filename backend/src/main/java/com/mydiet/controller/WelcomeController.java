package com.mydiet.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WelcomeController {

    @GetMapping("/welcome")
    @ResponseBody
    public String welcome() {
        return """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>MyDiet - 로그인</title>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        min-height: 100vh;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        padding: 20px;
                    }
                    .login-container {
                        background: white;
                        border-radius: 20px;
                        padding: 40px;
                        box-shadow: 0 20px 40px rgba(0,0,0,0.1);
                        text-align: center;
                        max-width: 400px;
                        width: 100%;
                    }
                    .logo { font-size: 4rem; margin-bottom: 20px; }
                    .title { font-size: 2rem; font-weight: bold; color: #333; margin-bottom: 10px; }
                    .subtitle { color: #666; margin-bottom: 40px; font-size: 1.1rem; }
                    .social-buttons { display: flex; flex-direction: column; gap: 15px; }
                    .social-btn {
                        display: flex; align-items: center; justify-content: center;
                        padding: 15px 20px; border: 2px solid #e1e5e9; border-radius: 12px;
                        background: white; color: #333; text-decoration: none;
                        font-size: 1.1rem; font-weight: 600; transition: all 0.3s ease; gap: 10px;
                    }
                    .social-btn:hover {
                        border-color: #667eea; background: rgba(102, 126, 234, 0.05);
                        transform: translateY(-2px); box-shadow: 0 8px 20px rgba(102, 126, 234, 0.15);
                    }
                    .google-btn { border-color: #4285f4; color: #4285f4; }
                    .google-btn:hover { background: #4285f4; color: white; }
                    .kakao-btn { border-color: #fee500; color: #3c1e1e; background: #fee500; }
                    .kakao-btn:hover { background: #fdd835; }
                </style>
            </head>
            <body>
                <div class="login-container">
                    <div class="logo">🍎</div>
                    <h1 class="title">MyDiet</h1>
                    <p class="subtitle">AI와 함께하는 스마트한 건강 관리</p>
                    
                    <div class="social-buttons">
                        <a href="/oauth2/authorization/google" class="social-btn google-btn">
                            <span>🔍</span>
                            Google로 시작하기
                        </a>
                        
                        <a href="/oauth2/authorization/kakao" class="social-btn kakao-btn">
                            <span>💬</span>
                            Kakao로 시작하기
                        </a>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
}