package com.dennis.assessment_service.service.quiz;

import com.dennis.assessment_service.dto.quiz.QuestionDTO;
import com.dennis.assessment_service.model.quiz.Answer;
import com.dennis.assessment_service.model.quiz.Question;
import com.dennis.assessment_service.model.quiz.Quiz;
import com.dennis.assessment_service.repository.quiz.QuestionRepository;
import com.dennis.assessment_service.repository.quiz.QuizRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class QuizService {


    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuestionAnswerMapper questionAnswerMapper;

    public QuizService(QuizRepository quizRepository, QuestionRepository questionRepository, QuestionAnswerMapper questionAnswerMapper) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.questionAnswerMapper = questionAnswerMapper;
    }

    public void addQuestionsToQuiz(UUID quizId, List<Question> questions, Integer duration) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));
        quiz.setDurationInMinutes(duration);

        for (Question question : questions) {
            question.setQuiz(quiz);
            if (question.getAnswers() != null) {
                for (Answer answer : question.getAnswers()) {
                    answer.setQuestion(question);
                }
            }
        }

        questionRepository.saveAll(questions);
    }

    public List<QuestionDTO> getQuestionsByQuizId(UUID quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));
        return questionAnswerMapper.convertToDTOs(quiz.getQuestions());
    }

}
