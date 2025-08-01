-- Oracle Free 초기 데이터 (간단 버전)

-- 시퀀스 생성 (존재하지 않을 때만)
BEGIN
  FOR seq IN (SELECT 'USER_SEQ' as seq_name FROM dual UNION ALL
              SELECT 'MEAL_LOG_SEQ' FROM dual UNION ALL
              SELECT 'EMOTION_LOG_SEQ' FROM dual UNION ALL
              SELECT 'WORKOUT_LOG_SEQ' FROM dual UNION ALL
              SELECT 'CLAUDE_RESPONSE_SEQ' FROM dual) LOOP
    BEGIN
      EXECUTE IMMEDIATE 'CREATE SEQUENCE ' || seq.seq_name || ' START WITH 1 INCREMENT BY 1 CACHE 10';
    EXCEPTION
      WHEN OTHERS THEN
        IF SQLCODE != -955 THEN -- ORA-00955: name is already used by an existing object
          RAISE;
        END IF;
    END;
  END LOOP;
END;
/

-- 관리자 계정 (중복 방지)
MERGE INTO MYDIET_USER U
USING (SELECT 'admin@mydiet.com' AS email FROM dual) S
ON (U.EMAIL = S.email)
WHEN NOT MATCHED THEN
  INSERT (USER_ID, EMAIL, PASSWORD, NICKNAME, WEIGHT_GOAL, TARGET_CALORIES, EMOTION_MODE, USER_ROLE, CREATED_AT)
  VALUES (USER_SEQ.NEXTVAL, 'admin@mydiet.com', 
          '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 
          '관리자', 70.0, 2000.0, '무자비', 'ADMIN', 
          TIMESTAMP '2025-01-01 00:00:00');

-- 테스트 사용자
MERGE INTO MYDIET_USER U
USING (SELECT 'user1@test.com' AS email FROM dual) S
ON (U.EMAIL = S.email)
WHEN NOT MATCHED THEN
  INSERT (USER_ID, EMAIL, PASSWORD, NICKNAME, WEIGHT_GOAL, TARGET_CALORIES, EMOTION_MODE, USER_ROLE, CREATED_AT)
  VALUES (USER_SEQ.NEXTVAL, 'user1@test.com', 
          '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 
          '다이어터1', 60.0, 1800.0, '츤데레', 'USER', 
          TIMESTAMP '2025-01-15 10:30:00');

COMMIT;
