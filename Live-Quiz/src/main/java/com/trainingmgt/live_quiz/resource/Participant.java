package com.trainingmgt.live_quiz.resource;

import lombok.Data;

@Data
public class Participant {
    private String userId;
    private String name;
    private int totalScore;
    private String avatar;
    private String role;

    public Participant(String userId, String name, int totalScore, String avatar, String role) {
        this.userId = userId;
        this.name = name;
        this.totalScore = totalScore;
        this.avatar = avatar;
        this.role = role;
    }
}
