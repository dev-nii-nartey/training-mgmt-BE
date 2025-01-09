package com.trainingmgt.live_quiz.response;

import jakarta.persistence.ElementCollection;
import lombok.Data;

import java.util.List;

@Data
public class GeneratedQuestion {
        private String questionText;
        private List<String> options;
        private String correctAnswer;
        @ElementCollection
        private List<String> tags;
}
