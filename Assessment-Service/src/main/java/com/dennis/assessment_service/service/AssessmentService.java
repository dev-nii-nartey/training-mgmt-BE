package com.dennis.assessment_service.service;

import com.dennis.assessment_service.dto.AssessmentDTO;
import com.dennis.assessment_service.model.Enum.AssessmentType;
import com.dennis.assessment_service.model.Presentation;
import com.dennis.assessment_service.model.lab.Lab;
import com.dennis.assessment_service.model.quiz.Quiz;
import com.dennis.assessment_service.repository.lab.LabRepository;
import com.dennis.assessment_service.repository.PresentationRepository;
import com.dennis.assessment_service.repository.quiz.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class AssessmentService {

    private final QuizRepository quizRepository;
    private final LabRepository labRepository;
    private final PresentationRepository presentationRepository;


    public AssessmentService(QuizRepository quizRepository, LabRepository labRepository, PresentationRepository presentationRepository2) {
        this.quizRepository = quizRepository;
        this.labRepository = labRepository;
        this.presentationRepository = presentationRepository2;
    }


    @Transactional
    public Quiz createQuiz(String title, String description, String focusArea, MultipartFile coverImage) {
        //TODO: search for quiz (using title and focusArea) if it exists, return the id
        try{
            Quiz quiz = Quiz.builder()
                    .title(title)
                    .description(description)
                    .assessmentType(AssessmentType.QUIZ)
                    .focusArea(focusArea)
                    .createdAt(LocalDateTime.now())
                    .coverImage(coverImage != null && !coverImage.isEmpty() ? coverImage.getBytes() : null)
                    .build();
            return quizRepository.save(quiz);
        }catch (IOException e) {
            throw new RuntimeException("failed to create quiz",e);
        }
    }

    @Transactional(readOnly = true)
    public List<AssessmentDTO> getAllQuizzes() {
        return quizRepository.findAll()
                .stream()
                .map(quiz -> AssessmentDTO.builder()
                        .id(quiz.getId())
                        .title(quiz.getTitle())
                        .assessmentType(quiz.getAssessmentType())
                        .focusArea(quiz.getFocusArea())
                        .createdAt(quiz.getCreatedAt())
                        .description(quiz.getDescription())
                        .coverImage(quiz.getCoverImage())
                        .build())
                .toList();
    }

    @Transactional
    public Lab createLab(String title, String description, String focusArea,MultipartFile coverImage, MultipartFile file) {
        try {
            Lab lab = Lab.builder()
                    .title(title)
                    .assessmentType(AssessmentType.LAB)
                    .description(description)
                    .focusArea(focusArea)
                    .createdAt(LocalDateTime.now())
                    .coverImage(coverImage != null && !coverImage.isEmpty() ? coverImage.getBytes() : null)
                    .file(file != null && !file.isEmpty() ? file.getBytes() : null)
                    .build();
            return labRepository.save(lab);
        } catch (IOException e) {
            throw new RuntimeException("failed to create lab", e);
        }
    }

    @Transactional(readOnly = true)
    public List<AssessmentDTO> getAllLabs() {
        return labRepository.findAll()
                .stream()
                .map(lab -> AssessmentDTO.builder()
                        .id(lab.getId())
                        .title(lab.getTitle())
                        .assessmentType(lab.getAssessmentType())
                        .focusArea(lab.getFocusArea())
                        .createdAt(lab.getCreatedAt())
                        .description(lab.getDescription())
                        .coverImage(lab.getCoverImage())
                        .build())
                .toList();
    }

    @Transactional
    public Presentation createPresentation(String title, String description, String focusArea,MultipartFile coverImage, MultipartFile file){
        try {
            Presentation presentation = Presentation.builder()
                    .title(title)
                    .description(description)
                    .assessmentType(AssessmentType.PRESENTATION)
                    .focusArea(focusArea)
                    .createdAt(LocalDateTime.now())
                    .coverImage(coverImage != null && !coverImage.isEmpty() ? coverImage.getBytes() : null)
                    .file(file != null && !file.isEmpty() ? file.getBytes() : null)
                    .build();
            return presentationRepository.save(presentation);
        } catch (IOException e) {
            throw new RuntimeException("failed to create lab", e);
        }

    }

    @Transactional(readOnly = true)
    public List<AssessmentDTO> getAllPresentations() {
        return presentationRepository.findAll()
                .stream()
                .map(presentation -> AssessmentDTO.builder()
                        .id(presentation.getId())
                        .title(presentation.getTitle())
                        .assessmentType(presentation.getAssessmentType())
                        .focusArea(presentation.getFocusArea())
                        .createdAt(presentation.getCreatedAt())
                        .description(presentation.getDescription())
                        .coverImage(presentation.getCoverImage())
                        .build())
                .toList();

    }


}
