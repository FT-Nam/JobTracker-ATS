package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.InterviewInterviewer;
import com.jobtracker.jobtracker_app.enums.InterviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface InterviewInterviewerRepository
        extends JpaRepository<InterviewInterviewer, String> {

    @Query("""
        SELECT ii
        FROM InterviewInterviewer ii
        WHERE ii.interviewer.id = :interviewerId
        AND ii.company.id = :companyId
        AND ii.interview.deletedAt IS NULL
        AND ii.interview.status IN :activeStatuses
        AND (:excludeInterviewId IS NULL 
             OR ii.interview.id <> :excludeInterviewId)
    """)
    Set<InterviewInterviewer> findActiveInterviewsOfInterviewer(
            @Param("interviewerId") String interviewerId,
            @Param("companyId") String companyId,
            @Param("activeStatuses") List<InterviewStatus> activeStatuses,
            @Param("excludeInterviewId") String excludeInterviewId
    );
}