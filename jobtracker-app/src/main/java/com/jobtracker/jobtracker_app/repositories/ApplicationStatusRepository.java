package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationStatusRepository extends JpaRepository<ApplicationStatus, String>, JpaSpecificationExecutor<ApplicationStatus> {
    Optional<ApplicationStatus> findByNameAndDeletedAtIsNull(String name);
    Optional<ApplicationStatus> findByIdAndDeletedAtIsNull(String id);
}

