package com.mydiet.dto;

public class AIQuestionRequest {
    private String question;
    private Long userId;
    
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}