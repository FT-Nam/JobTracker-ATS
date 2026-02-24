package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.InterviewInterviewer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewInterviewerRepository extends JpaRepository<InterviewInterviewer, String> {
}
