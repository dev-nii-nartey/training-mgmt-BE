package com.trainingmgt.live_quiz.resource;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Question {
    private UUID id;
    private String questionText;
    private List<String> options;
    private long duration;
}
