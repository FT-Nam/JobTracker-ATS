package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, String> {
}




