package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, String> {
    @Query("SELECT i FROM Interview i LEFT JOIN FETCH i.interviewers WHERE i.application.id = :applicationId")
    List<Interview> findByApplicationIdWithInterviewers(String applicationId);

    Optional<Interview> findByIdAndDeletedAtIsNull(String id);
}




