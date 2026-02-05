package com.jobtracker.jobtracker_app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracker.jobtracker_app.entities.JobStatus;

import java.util.Optional;

public interface JobStatusRepository extends JpaRepository<JobStatus, String> {
    Optional<JobStatus> findByName(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, String id);
}

