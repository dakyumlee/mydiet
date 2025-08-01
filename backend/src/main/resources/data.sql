-- 테스트 사용자 추가
INSERT INTO users (nickname, email, password, weight_goal, emotion_mode, created_at) VALUES
('다이어터123', 'test@test.com', 'password', 60.0, '무자비', CURRENT_TIMESTAMP),
('건강이', 'health@test.com', 'password', 55.0, '다정함', CURRENT_TIMESTAMP),
('운동맨', 'workout@test.com', 'password', 70.0, '츤데레', CURRENT_TIMESTAMP);

-- 오늘 식단 기록 추가
INSERT INTO meal_logs (user_id, description, calories_estimate, date) VALUES
(1, '아침: 토스트 2개', 300, CURRENT_DATE),
(1, '점심: 치킨샐러드', 450, CURRENT_DATE),
(2, '아침: 요거트', 150, CURRENT_DATE),
(3, '점심: 프로틴 쉐이크', 200, CURRENT_DATE);
