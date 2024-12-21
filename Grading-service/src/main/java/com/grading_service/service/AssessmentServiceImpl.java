package com.grading_service.service;


import com.grading_service.dto.*;
import com.grading_service.entity.*;
import com.grading_service.exceptions.BusinessValidationException;

import com.grading_service.repository.AssessmentRepository;
import com.grading_service.repository.TraineeGradeHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final TraineeGradeHistoryRepository gradeHistoryRepository;
    private final ProfileServiceClient profileServiceClient;

    public AssessmentServiceImpl(AssessmentRepository assessmentRepository, TraineeGradeHistoryRepository gradeHistoryRepository, ProfileServiceClient profileServiceClient) {
        this.assessmentRepository = assessmentRepository;
        this.gradeHistoryRepository = gradeHistoryRepository;
        this.profileServiceClient = profileServiceClient;
    }

    @Override
    public SubmittedAssessment submitAssessment(AssessmentRequest request) throws IOException {
        if (request.url() == null) {
            throw new BusinessValidationException("url is required");
        }
        SubmittedAssessment submittedAssessment = new SubmittedAssessment();
        submittedAssessment.setAssessmentId(request.assessmentId());
        submittedAssessment.setTitle(request.title());
        submittedAssessment.setLabByWeek(request.labByWeek());
        submittedAssessment.setUrl(request.url());
        submittedAssessment.setTraineeEmail(request.traineeId());
        submittedAssessment.setType(AssessmentType.LAB);
        submittedAssessment.setGraded(false);
        submittedAssessment.setDateSubmitted(LocalDateTime.now());

        if (request.file() != null) {
            submittedAssessment.setFile(request.file().getBytes());
        }

        return assessmentRepository.save(submittedAssessment);
    }

    @Override
    public SubmittedAssessment gradeQuiz(QuizSubmission request) {
        SubmittedAssessment submittedAssessment = new SubmittedAssessment();
        submittedAssessment.setAssessmentId(request.quizId());
        submittedAssessment.setTraineeEmail(request.traineeId());
        submittedAssessment.setType(AssessmentType.QUIZ);
        submittedAssessment.setUrl(null);
        submittedAssessment.setDateSubmitted(LocalDateTime.now());
        submittedAssessment.setTitle(request.title());
        submittedAssessment.setTotalMarks(request.totalMarks());
        submittedAssessment.setLetterGrade(GradeUtils.assignLetterGrade(request.totalMarks()));
        submittedAssessment.setGraded(true);
        return assessmentRepository.save(submittedAssessment);
    }

    @Override
    public SubmittedAssessment gradeAssessment(Grade grade) {
        SubmittedAssessment submittedAssessment = assessmentRepository.findByTitleAndTraineeEmailIgnoreCase(grade.assessmentTitle(),grade.traineeEmail());
        if (submittedAssessment.getType() == AssessmentType.QUIZ) {
            throw new BusinessValidationException("Quiz submittedAssessment cannot be assigned mark");
        }
        submittedAssessment.setTotalMarks(grade.marks());
        submittedAssessment.setLetterGrade(GradeUtils.assignLetterGrade(grade.marks()));
        submittedAssessment.setGraded(true);
        return assessmentRepository.save(submittedAssessment);
    }

    @Override
    public SubmittedAssessment getAssessmentDetails(String title, String email) {
        SubmittedAssessment byTitleAndTraineeEmailIgnoreCase = assessmentRepository.findByTitleAndTraineeEmailIgnoreCase(title, email);
if (byTitleAndTraineeEmailIgnoreCase == null) {
    throw new BusinessValidationException("No such assessment");
}
return byTitleAndTraineeEmailIgnoreCase;
    }

    @Override
    public List<TraineeAssessmentDetailsDto> getTraineeGradedAssessment(String traineeEmail) {
        // Get graded assessments
        List<SubmittedAssessment> gradedAssessments = assessmentRepository.findByTraineeEmailAndGraded(traineeEmail, true);
        if (gradedAssessments == null) {
          throw new BusinessValidationException("This assessment is not found for this email ");
        }
        // Create single assessment map for profile service
        Map<String, SubmittedAssessment> singleAssessmentMap = Map.of(traineeEmail,
                gradedAssessments.isEmpty() ? null : gradedAssessments.getFirst());

        // Fetch trainee profile
        List<TraineeDto> traineeProfiles = profileServiceClient.getTraineesByEmails(
                Set.of(traineeEmail),
                singleAssessmentMap
        );

        // Get the trainee profile (will use fallback values if service is down)
        TraineeDto traineeProfile = traineeProfiles.isEmpty() ?
                new TraineeDto("FirstName Unavailable", "LastName Unavailable",
                        "Specialization Unavailable", 0.0, traineeEmail,traineeProfiles.getFirst().url())
                : traineeProfiles.getFirst();

        // Convert and combine the data
        return gradedAssessments.stream()
                .map(assessment -> convertToAssessmentDetailsDto(assessment, traineeProfile))
                .collect(Collectors.toList());
    }

    private TraineeAssessmentDetailsDto convertToAssessmentDetailsDto(
            SubmittedAssessment assessment,
            TraineeDto traineeProfile) {
        TraineeAssessmentDetailsDto dto = new TraineeAssessmentDetailsDto();

        // Copy assessment details
        dto.setId(assessment.getId());
        dto.setAssessmentId(assessment.getAssessmentId());
        dto.setTitle(assessment.getTitle());
        dto.setType(assessment.getType().toString());
        dto.setTraineeEmail(assessment.getTraineeEmail());
        dto.setDateSubmitted(assessment.getDateSubmitted());
        dto.setTotalMarks(assessment.getTotalMarks());
        dto.setLetterGrade(assessment.getLetterGrade());
        dto.setGraded(assessment.isGraded());

        // Add trainee profile information
        dto.setFirstName(traineeProfile.firstName());
        dto.setLastName(traineeProfile.LastName());
        dto.setSpecialization(traineeProfile.specialization());

        return dto;
    }

    @Override
    public List<Graded> getGradedAssessment() {
        // Get all graded assessments
        List<SubmittedAssessment> gradedSubmittedAssessments = assessmentRepository.findAssessmentsByGradedIs(true);

        // Create a list to store our final results
        List<Graded> result = new ArrayList<>();

// Step 1: Create a map to store assessments grouped by their title
// The key will be the title (String), and the value will be a list of assessments with that title
        Map<String, List<SubmittedAssessment>> groupsByTitle = new HashMap<>();

// Step 2: Group all assessments by their title
        for (SubmittedAssessment submittedAssessment : gradedSubmittedAssessments) {
            String title = submittedAssessment.getTitle();

            // If we haven't seen this title before, create a new list for it
            if (!groupsByTitle.containsKey(title)) {
                groupsByTitle.put(title, new ArrayList<>());
            }

            // Add the submittedAssessment to its title group
            groupsByTitle.get(title).add(submittedAssessment);
        }

// Step 3: Convert each group into a Graded object
        for (String title : groupsByTitle.keySet()) {
            // Get the list of assessments for this title
            List<SubmittedAssessment> assessmentsWithSameTitle = groupsByTitle.get(title);

            // Get the first assessment from the group - we'll use its details
            Graded gradedItem = getGraded(assessmentsWithSameTitle);

            // Add this Graded object to our results
            result.add(gradedItem);
        }

// Step 4: Sort the results by title
// We'll use a simple way to sort the list by title
        result.sort(new Comparator<>() {
            @Override
            public int compare(Graded a, Graded b) {
                return a.title().compareTo(b.title());
            }
        });

// Return the final sorted list
        return result;
    }

    private static Graded getGraded(List<SubmittedAssessment> assessmentsWithSameTitle) {
        SubmittedAssessment firstSubmittedAssessment = assessmentsWithSameTitle.getFirst();

        // Create a new Graded object with:
        // - details from the first assessment
        // - count of how many assessments have this title
        // use ID from first assessment
        // use title from first assessment
        // use date from first assessment
        // use type from first assessment
        // count of assessments with this title
        return new Graded(
                firstSubmittedAssessment.getAssessmentId(),          // use ID from first assessment
                firstSubmittedAssessment.getTitle(),       // use title from first assessment
                firstSubmittedAssessment.getDateSubmitted(), // use date from first assessment
                firstSubmittedAssessment.getType(),        // use type from first assessment
                assessmentsWithSameTitle.size()   // count of assessments with this title
        );
    }

    @Override
    public List<SubmittedAssessment> getAssessmentGrades() {
        return assessmentRepository.findAssessmentsByGradedIs(true);
    }


    public List<Graded> getUngradedGradedAssessment() {
        List<SubmittedAssessment> ungradedSubmittedAssessments = assessmentRepository.findAssessmentsByGradedIs(false);
        // Create a list to store our final results
        List<Graded> result = new ArrayList<>();

// Step 1: Create a map to store assessments grouped by their title
// The key will be the title (String), and the value will be a list of assessments with that title
        Map<String, List<SubmittedAssessment>> groupsByTitle = new HashMap<>();

// Step 2: Group all assessments by their title
        for (SubmittedAssessment submittedAssessment : ungradedSubmittedAssessments) {
            String title = submittedAssessment.getTitle();

            // If we haven't seen this title before, create a new list for it
            if (!groupsByTitle.containsKey(title)) {
                groupsByTitle.put(title, new ArrayList<>());
            }

            // Add the submittedAssessment to its title group
            groupsByTitle.get(title).add(submittedAssessment);
        }

// Step 3: Convert each group into a Graded object
        for (String title : groupsByTitle.keySet()) {
            // Get the list of assessments for this title
            List<SubmittedAssessment> assessmentsWithSameTitle = groupsByTitle.get(title);

            // Get the first assessment from the group - we'll use its details
            Graded gradedItem = getGraded(assessmentsWithSameTitle);

            // Add this Graded object to our results
            result.add(gradedItem);
        }

// Step 4: Sort the results by title
// We'll use a simple way to sort the list by title
        result.sort(new Comparator<>() {
            @Override
            public int compare(Graded a, Graded b) {
                return a.title().compareTo(b.title());
            }
        });

// Return the final sorted list
        return result;
    }

    @Override
    public List<TraineeGradeHistoryDto> getAllTraineesGradeHistory() {
        // Get graded assessments for current page
        List<SubmittedAssessment> gradedAssessmentsPage = getAssessmentGrades();

        // Process the current page
        Map<String, List<SubmittedAssessment>> assessmentsByTrainee = groupAssessmentsByTrainee(gradedAssessmentsPage);

        // Create a map of email to single assessment for profile service
        Map<String, SubmittedAssessment> singleAssessmentMap = assessmentsByTrainee.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getFirst()
                ));

        // Fetch trainee profiles
        List<TraineeDto> traineeProfiles = profileServiceClient.getTraineesByEmails(
                assessmentsByTrainee.keySet(),
                singleAssessmentMap
        );

        // Create a map of email to trainee profile for easy lookup
        Map<String, TraineeDto> profileMap = traineeProfiles.stream()
                .collect(Collectors.toMap(
                        TraineeDto::email,
                        trainee -> trainee
                ));

        return processTraineeHistories(assessmentsByTrainee, profileMap);
    }

    // Modified - updated signature and implementation
    private List<TraineeGradeHistoryDto> processTraineeHistories(
            Map<String, List<SubmittedAssessment>> assessmentsByTrainee,
            Map<String, TraineeDto> profileMap) {
        return assessmentsByTrainee.entrySet().stream()
                .map(entry -> calculateTraineeHistory(
                        entry.getKey(),
                        entry.getValue(),
                        profileMap.getOrDefault(entry.getKey(), null)
                ))
                .collect(Collectors.toList());
    }

    // Modified - added traineeProfile parameter
    private TraineeGradeHistoryDto calculateTraineeHistory(
            String traineeId,
            List<SubmittedAssessment> submittedAssessments,
            TraineeDto traineeProfile) {
        TraineeGradeHistory history = getOrCreateGradeHistory(traineeId);
        AssessmentStatistics stats = calculateAssessmentStatistics(submittedAssessments);
        updateGradeHistory(history, stats);

        history = gradeHistoryRepository.save(history);
        return convertToDTO(history, submittedAssessments.size(), traineeProfile);
    }

    // Modified - added traineeProfile parameter
    private TraineeGradeHistoryDto convertToDTO(
            TraineeGradeHistory history,
            int totalAssessments,
            TraineeDto traineeProfile) {
        TraineeGradeHistoryDto dto = new TraineeGradeHistoryDto();
        setBasicInfo(dto, history, traineeProfile);
        setAssessmentCounts(dto, history);
        setAverageGrade(dto, history, totalAssessments);
        return dto;
    }

    // Modified - added traineeProfile parameter and name setting
    private void setBasicInfo(
            TraineeGradeHistoryDto dto,
            TraineeGradeHistory history,
            TraineeDto traineeProfile) {
        dto.setId(history.getId());
        dto.setTraineeEmail(history.getTraineeEmail());
        dto.setOverallGradePoints(history.getOverallGradePoints());

        // Set name from profile, or use defaults if profile is null
        if (traineeProfile != null) {
            dto.setFirstName(traineeProfile.firstName());
            dto.setLastName(traineeProfile.LastName());
        } else {
            dto.setFirstName("FirstName Unavailable");
            dto.setLastName("LastName Unavailable");
        }
    }

    private Map<String, List<SubmittedAssessment>> groupAssessmentsByTrainee(List<SubmittedAssessment> submittedAssessments) {
        return submittedAssessments.stream()
                .collect(Collectors.groupingBy(SubmittedAssessment::getTraineeEmail));
    }
    


    private TraineeGradeHistory getOrCreateGradeHistory(String traineeId) {
        TraineeGradeHistory history = gradeHistoryRepository.findByTraineeEmail(traineeId);
        if (history == null) {
            history = new TraineeGradeHistory();
            history.setTraineeEmail(traineeId);
        }
        return history;
    }

    private AssessmentStatistics calculateAssessmentStatistics(List<SubmittedAssessment> submittedAssessments) {
        AssessmentStatistics stats = new AssessmentStatistics();

        for (SubmittedAssessment submittedAssessment : submittedAssessments) {
            stats.totalPoints += submittedAssessment.getTotalMarks();
            switch (submittedAssessment.getType()) {
                case QUIZ -> stats.quizCount++;
                case LAB -> stats.labCount++;
                case PRESENTATION -> stats.presentationCount++;
            }
        }

        return stats;
    }

    private void updateGradeHistory(TraineeGradeHistory history, AssessmentStatistics stats) {
        history.setOverallGradePoints(stats.totalPoints);
        history.setGradedQuizzesCount(stats.quizCount);
        history.setGradedLabsCount(stats.labCount);
        history.setGradedPresentationsCount(stats.presentationCount);
    }


    private void setAssessmentCounts(TraineeGradeHistoryDto dto, TraineeGradeHistory history) {
        dto.setGradedQuizzesCount(history.getGradedQuizzesCount());
        dto.setGradedLabsCount(history.getGradedLabsCount());
        dto.setGradedPresentationsCount(history.getGradedPresentationsCount());
    }

    private void setAverageGrade(TraineeGradeHistoryDto dto, TraineeGradeHistory history, int totalAssessments) {
        double average = calculateAverageGrade(history.getOverallGradePoints(), totalAssessments);
        dto.setAverageGradePoints(average);
    }

    private double calculateAverageGrade(double totalPoints, int totalAssessments) {
        return totalAssessments > 0 ? totalPoints / totalAssessments : 0.0;
    }

    /**
     * Helper class to store assessment statistics
     */
    private static class AssessmentStatistics {
        double totalPoints = 0;
        int quizCount = 0;
        int labCount = 0;
        int presentationCount = 0;
    }


    @Override
    public TraineeGradeHistory getTraineeGradeHistory(String traineeId) {
        List<SubmittedAssessment> submittedAssessments = assessmentRepository.findByTraineeEmailAndGraded(traineeId, true);

        TraineeGradeHistory history = gradeHistoryRepository.findByTraineeEmail(traineeId);


        double totalPoints = 0;
        int quizCount = 0, labCount = 0, presentationCount = 0;

        for (SubmittedAssessment submittedAssessment : submittedAssessments) {
            totalPoints += submittedAssessment.getTotalMarks();
            switch (submittedAssessment.getType()) {
                case QUIZ -> quizCount++;
                case LAB -> labCount++;
                case PRESENTATION -> presentationCount++;
            }
        }

        if (history == null) {
            history = new TraineeGradeHistory();
            history.setTraineeEmail(traineeId);
        }

        history.setOverallGradePoints(totalPoints);
        history.setGradedQuizzesCount(quizCount);
        history.setGradedLabsCount(labCount);
        history.setGradedPresentationsCount(presentationCount);

        return gradeHistoryRepository.save(history);
    }

    @Override
    public List<TraineeDto> getAssessmentsByTitle(String title,boolean status) {
        if (title == null) {
            throw new BusinessValidationException("Title should not be null");
        }
        title = title.trim();
        List<SubmittedAssessment> assessments = assessmentRepository.findByTitleIgnoreCaseAndGradedIs(title, status);

        // Create a map of traineeId to their assessment for easy lookup
        Map<String, SubmittedAssessment> traineeAssessments = assessments.stream()
                .collect(Collectors.toMap(
                        SubmittedAssessment::getTraineeEmail,
                        assessment -> assessment
                ));

        Set<String> traineeEmails = traineeAssessments.keySet();

        // Pass both emails and assessments map to the client
        return profileServiceClient.getTraineesByEmails(traineeEmails, traineeAssessments);
    }

}