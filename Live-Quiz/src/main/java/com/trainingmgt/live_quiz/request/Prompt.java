package com.trainingmgt.live_quiz.request;

import lombok.Data;

@Data
public class Prompt {
    String topic;
    String level;
    String questionType;
    String includeCode;
}
