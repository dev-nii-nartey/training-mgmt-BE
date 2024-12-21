package com.trainingmgt.live_quiz.service;

import com.trainingmgt.live_quiz.enums.Role;
import com.trainingmgt.live_quiz.model.LiveQuiz;
import com.trainingmgt.live_quiz.model.Question;
import com.trainingmgt.live_quiz.repository.QuizRepository;
import com.trainingmgt.live_quiz.request.CreateRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LiveQuizService {
    private final QuizRepository quizRepository;
    private final AuthService authService;

    public LiveQuizService(QuizRepository quizRepository, AuthService authService) {
        this.quizRepository = quizRepository;
        this.authService = authService;
    }

    public UUID createLiveQuiz(List<CreateRequest> request) {
        if(authService.getUserRole().equals(Role.TRAINEE)){
            throw new IllegalStateException("Only trainer/admin can create live quiz");
        }
        LiveQuiz liveQuiz = new LiveQuiz();
        liveQuiz.setHost(authService.getUserId());
        List<Question> questions = request.stream().map(req -> {
            Question question = new Question();
            question.setQuestionText(req.getQuestionText());
            question.setOptions(req.getOptions());
            question.setCorrectAnswer(req.getCorrectAnswer());
            question.setDuration(req.getTimeLimit().getValue());
            question.setPoints(req.getPoints().getValue().intValue());
            question.setLiveQuiz(liveQuiz);
            return question;
        }).toList();

        liveQuiz.setQuestions(questions);

        quizRepository.save(liveQuiz);

        return liveQuiz.getId();
    }




}