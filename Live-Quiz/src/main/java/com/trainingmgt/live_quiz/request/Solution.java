package com.trainingmgt.live_quiz.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.util.UUID;

@Data
public class Solution {
    @NotNull(message = "At least one selection is required")
    private String choice;
    UUID questionId;
}
