package com.trainingmgt.live_quiz.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    LocalDateTime timestamp;
    String message;
    int status;
    String error;
    String path;
}
