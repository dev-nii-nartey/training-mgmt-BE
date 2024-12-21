package com.grading_service.repository;

import com.grading_service.entity.TraineeGradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TraineeGradeHistoryRepository extends JpaRepository<TraineeGradeHistory,Long> {
    TraineeGradeHistory findByTraineeEmail(String traineeId);
}