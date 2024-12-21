package com.trainingmgt.live_quiz.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateRequest {
    private String questionText;
    private List<String> options;
    private String correctAnswer;
    private Extra timeLimit;
    private Extra points;
}