package com.trainingmgt.live_quiz.service;

import com.trainingmgt.live_quiz.enums.QuestionStatus;
import com.trainingmgt.live_quiz.enums.LiveQuizStatus;
import com.trainingmgt.live_quiz.model.Answer;
import com.trainingmgt.live_quiz.model.LiveQuiz;
import com.trainingmgt.live_quiz.model.PlayerScore;
import com.trainingmgt.live_quiz.model.Question;
import com.trainingmgt.live_quiz.repository.AnswerRepository;
import com.trainingmgt.live_quiz.repository.PlayerScoreRepository;
import com.trainingmgt.live_quiz.repository.QuestionRepository;
import com.trainingmgt.live_quiz.repository.QuizRepository;
import com.trainingmgt.live_quiz.request.Solution;
import com.trainingmgt.live_quiz.resource.Participant;
import jakarta.validation.Valid;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class LiveQuizStateManager {
    private final QuizRepository quizRepository;
    private final PlayerScoreRepository playerScoreRepository;
    private final WebSocketService webSocketService;
    private final TaskScheduler taskScheduler;
    private final AnswerRepository answerRepository;
    private final UserService userService;
    private final QuestionRepository questionRepository;

    public LiveQuizStateManager(
            QuizRepository quizRepository, PlayerScoreRepository playerScoreRepository,
            WebSocketService webSocketService, TaskScheduler taskScheduler, AnswerRepository answerRepository,
            UserService userService, QuestionRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.playerScoreRepository = playerScoreRepository;
        this.webSocketService = webSocketService;
        this.taskScheduler = taskScheduler;
        this.answerRepository = answerRepository;
        this.userService = userService;
        this.questionRepository = questionRepository;
    }

    public void startGame(UUID quizId) {
        LiveQuiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        if(quiz.getStatus() == LiveQuizStatus.IN_PROGRESS){
            throw new IllegalArgumentException("Game is already in progress");
        }
        quiz.setStatus(LiveQuizStatus.IN_PROGRESS);
        quiz.setStartTime(LocalDateTime.now());
        quizRepository.save(quiz);
        broadcastQuestion(quiz, 0,5);
    }

    public void endGame(UUID quizId) {
        LiveQuiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        if(quiz.getStatus()!= LiveQuizStatus.IN_PROGRESS){
            throw new IllegalArgumentException("Game is not in progress");
        }
        quiz.setStatus(LiveQuizStatus.ENDED);
        quizRepository.save(quiz);
        broadcastQuestion(quiz, quiz.getQuestions().size()+1,0);
    }

    private void broadcastQuestion(LiveQuiz quiz, int questionIndex,int delays) {
        if (questionIndex >= quiz.getQuestions().size()) {
            List<Participant> leaderboard = playerScoreRepository.findByQuizId(quiz.getId()).stream()
                    .map(score -> {
                        String userId = score.getUserId();
                        return userService.getPlayerDetails(userId);
                    })
                    .toList();
            webSocketService.broadcastLiveQuizEnd(quiz.getId(),leaderboard);
            return;
        }

        Question question = quiz.getQuestions().get(questionIndex);
        question.setStatus(QuestionStatus.ACTIVE);
        questionRepository.save(question);
        webSocketService.broadcastQuestion(quiz.getId(), question);

        long durationMillis = question.getDuration()+delays;
        long intervalMillis = 1000;

        for (long remaining = durationMillis; remaining > 0; remaining -= intervalMillis) {
            long finalRemaining = remaining;
            taskScheduler.schedule(() ->
                            webSocketService.broadcastTimerUpdate(quiz.getId(), finalRemaining),
                    Instant.now().plusMillis(durationMillis - remaining)
            );
        }

        taskScheduler.schedule(() -> {
            scorePlayers(quiz, question);
            broadcastQuestion(quiz, questionIndex + 1,delays);
        }, Instant.now().plusMillis(durationMillis));
    }


    public void submitAnswer(@Valid Solution answer, UUID quizId) {
        LiveQuiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));

        Question question = quiz.getQuestions().stream()
                .filter(q -> q.getId().equals(answer.getQuestionId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        if (!question.getStatus().equals(QuestionStatus.ACTIVE)) {
            throw new IllegalStateException("Duration to answer this question has expired");
        }

        String userId = userService.getUserId();

        Answer existingAnswer = answerRepository.findByUserIdAndQuestionId(userId, question.getId())
                .orElse(null);

        if (existingAnswer != null) {
            existingAnswer.setSelectedAnswer(answer.getChoice());
            existingAnswer.setSubmittedAt(LocalDateTime.now());
            answerRepository.save(existingAnswer);
        } else {
            Answer newAnswer = new Answer();
            newAnswer.setUserId(userId);
            newAnswer.setQuestion(question);
            newAnswer.setQuiz(quiz);
            newAnswer.setSelectedAnswer(answer.getChoice());
            newAnswer.setSubmittedAt(LocalDateTime.now());
            answerRepository.save(newAnswer);
        }

        long answerCount = answerRepository.countByQuizIdAndQuestionId(quiz.getId(), question.getId());
        webSocketService.broadcastSubmission(quiz.getId(), question.getId(), answerCount);
    }

    public void joinQuiz(UUID gameId) {
        LiveQuiz game = quizRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        if (!game.getPlayers().contains(userService.getUserId())) {
            game.getPlayers().add(userService.getUserId());
            quizRepository.save(game);
        }
        webSocketService.broadcastLobbyUpdate(gameId, game.getPlayers());

    }

    private void scorePlayers(LiveQuiz quiz, Question question) {
        List<Answer> answers = answerRepository.findByQuizIdAndQuestionId(quiz.getId(), question.getId());

        // Prepare a map to count responses for each option
        Map<String, Long> responseDistribution = new HashMap<>();
        for (String option : question.getOptions()) {
            responseDistribution.put(option, 0L);
        }

        for (Answer answer : answers) {
            // Update the response distribution
            responseDistribution.merge(answer.getSelectedAnswer(), 1L, Long::sum);

            // Award points to correct answers
            if (question.getCorrectAnswer().equals(answer.getSelectedAnswer())) {
                PlayerScore playerScore = playerScoreRepository
                        .findByQuizIdAndUserId(quiz.getId(), answer.getUserId())
                        .orElseGet(() -> new PlayerScore(quiz, answer.getUserId()));
                playerScore.setTotalScore(playerScore.getTotalScore() + question.getPoints());
                playerScoreRepository.save(playerScore);
            }
        }

        // Mark the question as solved
        question.setStatus(QuestionStatus.SOLVED);
        questionRepository.save(question);

        // Broadcast the response distribution and correct answer
        List<String> distribution = question.getOptions().stream()
                .map(option -> option + ": " + responseDistribution.get(option))
                .toList();
        webSocketService.broadcastDistribution(question.getId(), distribution, question.getCorrectAnswer());
    }
}



