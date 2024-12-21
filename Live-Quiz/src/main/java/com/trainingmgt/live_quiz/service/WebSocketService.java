package com.trainingmgt.live_quiz.service;

import com.trainingmgt.live_quiz.model.Question;
import com.trainingmgt.live_quiz.resource.Participant;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastQuestion(UUID quizId, Question question) {
        messagingTemplate.convertAndSend("/topic/game/" + quizId, question);
    }


    public void broadcastLiveQuizEnd(UUID quizId,List<Participant> leaderboard) {
        Map<String, Object> payload = Map.of(
                "quizId", quizId,
                "leaderboard", leaderboard
        );
        messagingTemplate.convertAndSend("/topic/game/" + quizId, payload);
    }

    public void broadcastSubmission(UUID quizId, UUID questionId, Long submissionCount) {
        messagingTemplate.convertAndSend(
                "/topic/game/" + quizId + "/question/" + questionId + "/submissions",
                submissionCount
        );
    }

    public void broadcastTimerUpdate(UUID quizId, long remainingTime) {
        messagingTemplate.convertAndSend("/topic/game/" + quizId + "/timer", remainingTime);
    }

    public void broadcastDistribution(UUID questionId, List<String> distribution,String correctAnswer) {
        Map<String, Object> payload = Map.of(
                "questionId", questionId,
                "distribution", distribution,
                "correctAnswer",correctAnswer
        );
        messagingTemplate.convertAndSend("/topic/game/" + questionId + "/distribution", payload);
    }

    public void broadcastLobbyUpdate(UUID quizId, List<String> players) {
        Map<String, Object> lobbyState = new HashMap<>();
        lobbyState.put("players", players);

        messagingTemplate.convertAndSend("/topic/game/" + quizId + "/lobby", lobbyState);
    }

    public void playerConnected(UUID quizId, String playerId) {
        messagingTemplate.convertAndSend("/topic/game/" + quizId + "/player/connected", playerId);
    }

    public void playerDisconnected(UUID quizId, String playerId) {
        messagingTemplate.convertAndSend("/topic/game/" + quizId + "/player/disconnected", playerId);
    }
}
