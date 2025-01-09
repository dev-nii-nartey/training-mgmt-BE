package com.dennis.assessment_service.service.quiz;

import com.dennis.assessment_service.dto.quiz.GradeQuizDTO;
import com.dennis.assessment_service.dto.quiz.QuizSubmissionDTO;
import com.dennis.assessment_service.model.quiz.*;
import com.dennis.assessment_service.repository.quiz.AnswerRepository;
import com.dennis.assessment_service.repository.quiz.QuizRepository;
import com.dennis.assessment_service.repository.quiz.QuizSubmissionRepository;
import com.dennis.assessment_service.service.AssignmentService;
import com.dennis.assessment_service.service.GradeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuizSubmissionService {
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final AnswerRepository answerRepository;
    private final QuizRepository quizRepository;
    private final AssignmentService assignmentService;
    private final GradeService gradeService;

    public QuizSubmissionService(QuizSubmissionRepository quizSubmissionRepository, AnswerRepository answerRepository, QuizRepository quizRepository, AssignmentService assignmentService, GradeService gradeService) {
        this.quizSubmissionRepository = quizSubmissionRepository;
        this.answerRepository = answerRepository;
        this.quizRepository = quizRepository;
        this.assignmentService = assignmentService;
        this.gradeService = gradeService;
    }

    public String submitQuiz(UUID quizId, QuizSubmission submission) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));

        boolean isAssigned = assignmentService.existsByTraineeEmailAndAssessmentId(submission.getTraineeEmail(), quizId);
        if (!isAssigned) {
            throw new EntityNotFoundException("Trainee has not been assigned this quiz.");
        }

        submission.setQuiz(quiz);

        int totalMarks = quiz.getQuestions().stream()
                .mapToInt(Question::getMark)
                .sum();

        double score = 0;
        for (SubmittedAnswer submittedAnswer : submission.getSubmittedAnswers()) {
            Answer answer = answerRepository.findById(submittedAnswer.getSelectedAnswerId())
                    .orElseThrow(() -> new IllegalArgumentException("Answer not found"));
            Question question = answer.getQuestion();

            if (answer.isCorrect()) {
                score += question.getMark();
            }

            submittedAnswer.setQuizSubmission(submission);
        }


        double percentage = (score / totalMarks) * 100;
        submission.setScore((int) percentage);

        quizSubmissionRepository.save(submission);

        assignmentService.markAsSubmitted(submission.getTraineeEmail(), quizId, (int) percentage);

        GradeQuizDTO gradeQuizDTO = GradeQuizDTO.builder()
                .quizId(quizId)
                .traineeId(submission.getTraineeEmail())
                .title(quiz.getTitle())
                .totalMarks((double) totalMarks)
                .build();
        String grade = gradeService.sendQuizToGradeService(gradeQuizDTO);
        return switch (grade) {
            case "A+" -> "Outstanding! You have excelled in this assessment. Keep up the phenomenal work!";
            case "A" -> "Great job! Your hard work and dedication are paying off. Keep aiming high!";
            case "B+" -> "Well done! You're doing great, but there's always room to push a bit further.";
            case "B" -> "Good effort! Keep working on it, and you'll achieve even greater results.";
            case "C" -> "Not bad, but there's room for improvement. Stay focused and keep practicing.";
            case "D" -> "Don't be discouraged! This is a learning opportunity—keep at it and you'll get there.";
            case "E" -> "Keep pushing forward! Use this as motivation to sharpen your skills.";
            case "F" -> "It's okay to stumble sometimes. Reflect on this attempt, and come back stronger next time!";
            default -> "An unexpected error occurred. Alert your trainer to regrade your submission.";
        };

    }
    public List<QuizSubmissionDTO> getSubmissionsByQuizId(UUID quizId) {
        return quizSubmissionRepository.findByQuizId(quizId).stream()
                .map(submission -> QuizSubmissionDTO.builder()
                        .id(submission.getId())
                        .traineeEmail(submission.getTraineeEmail())
                        .submittedAt(submission.getSubmittedAt())
                        .score(submission.getScore())
                        .build())
                .collect(Collectors.toList());
    }
}
