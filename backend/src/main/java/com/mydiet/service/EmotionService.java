package com.mydiet.service;

import com.mydiet.dto.EmotionRequest;
import com.mydiet.entity.EmotionLog;
import com.mydiet.entity.User;
import com.mydiet.repository.EmotionLogRepository;
import com.mydiet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmotionService {

    private final EmotionLogRepository emotionLogRepository;
    private final UserRepository userRepository;

    public EmotionLog saveEmotion(EmotionRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow();
        
        EmotionLog emotionLog = EmotionLog.builder()
                .user(user)
                .mood(request.getMood())
                .note(request.getNote())
                .date(LocalDate.now())
                .build();
        
        return emotionLogRepository.save(emotionLog);
    }

    @Transactional(readOnly = true)
    public List<EmotionLog> getTodayEmotions(Long userId) {
        return emotionLogRepository.findByUserIdAndDate(userId, LocalDate.now());
    }
}
