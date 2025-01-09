package com.dennis.assessment_service.service.quiz;

import com.dennis.assessment_service.dto.quiz.AnswerDTO;
import com.dennis.assessment_service.dto.quiz.QuestionDTO;
import com.dennis.assessment_service.model.quiz.Answer;
import com.dennis.assessment_service.model.quiz.Question;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionAnswerMapper {

    public QuestionDTO convertToDTO(Question question) {
        List<AnswerDTO> answerDTOs = question.getAnswers().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return QuestionDTO.builder()
                .id(question.getId())
                .text(question.getText())
                .answersDTO(answerDTOs)
                .build();
    }

    public AnswerDTO convertToDTO(Answer answer) {
        return AnswerDTO.builder()
                .id(answer.getId())
                .text(answer.getText())
                .build();
    }

    public List<QuestionDTO> convertToDTOs(List<Question> questions) {
        return questions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}

