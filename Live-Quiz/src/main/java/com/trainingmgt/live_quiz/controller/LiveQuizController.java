package com.trainingmgt.live_quiz.controller;

import com.trainingmgt.live_quiz.service.AI.AIML;
import com.trainingmgt.live_quiz.enums.QuestionType;
import com.trainingmgt.live_quiz.model.LiveQuiz;
import com.trainingmgt.live_quiz.repository.QuizRepository;
import com.trainingmgt.live_quiz.request.CreateRequest;
import com.trainingmgt.live_quiz.request.Prompt;
import com.trainingmgt.live_quiz.request.Solution;
import com.trainingmgt.live_quiz.response.GeneratedQuestion;
import com.trainingmgt.live_quiz.service.LiveQuizService;
import com.trainingmgt.live_quiz.service.LiveQuizStateManager;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quiz")
public class LiveQuizController {
    private final AIML questionGenerationStrategy;
    private final LiveQuizService liveQuizService;
    private final LiveQuizStateManager liveQuizStateManager;
    private final QuizRepository quizRepository;


    public LiveQuizController(AIML questionGenerationStrategy, LiveQuizService liveQuizService, LiveQuizStateManager liveQuizStateManager, QuizRepository quizRepository) {
        this.questionGenerationStrategy = questionGenerationStrategy;
        this.liveQuizService = liveQuizService;
        this.liveQuizStateManager = liveQuizStateManager;
        this.quizRepository = quizRepository;
    }

    @PostMapping("/generate")
    public Iterable<GeneratedQuestion> generateQuiz(@RequestBody Prompt prompt) {
        return switch (QuestionType.valueOf(prompt.getQuestionType())) {
            case MULTIPLE_CHOICE -> questionGenerationStrategy.generateMultipleChoiceQuestions(prompt);
            case TRUE_FALSE -> questionGenerationStrategy.generateTrueFalseQuestions(prompt);
            case IMAGE_BASED -> questionGenerationStrategy.generateImageQuestions(prompt);
            default -> throw new IllegalArgumentException("Invalid question type");
        };
    }

    @GetMapping()
    public Iterable<LiveQuiz> getQuiz() {
       return quizRepository.findAll();
    }

    @PostMapping("/create")
    public UUID createQuiz(@RequestBody List<CreateRequest> request) {
        return liveQuizService.createLiveQuiz(request);
    }

    @PostMapping("/{quizId}/start")
    public void startGame(@PathVariable UUID quizId) {
        liveQuizStateManager.startGame(quizId);
    }

    @PostMapping("/{quizId}/stop")
    public void endGame(@PathVariable UUID quizId) {
        liveQuizStateManager.endGame(quizId);
    }

    @PostMapping("/{quizId}/join")
    public void joinLobby(@PathVariable UUID quizId) {
        liveQuizStateManager.joinQuiz(quizId);
    }

    @PostMapping("/{quizId}/submit")
    public void submitAnswer(@RequestBody @Valid Solution answer, @PathVariable UUID quizId) {
        liveQuizStateManager.submitAnswer(answer,quizId);
    }
}