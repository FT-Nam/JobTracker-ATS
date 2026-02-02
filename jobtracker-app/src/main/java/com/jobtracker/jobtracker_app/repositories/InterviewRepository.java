package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewRepository extends JpaRepository<Interview, String> {
}




