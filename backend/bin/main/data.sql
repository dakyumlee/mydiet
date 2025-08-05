-- 테스트 사용자 생성
INSERT INTO users (nickname, email, weight_goal, emotion_mode, created_at) 
VALUES ('테스트유저', 'test@mydiet.com', 65.0, '무자비', NOW()) 
ON CONFLICT DO NOTHING;
