package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String>, JpaSpecificationExecutor<Application> {
    Optional<Application> findByIdAndDeletedAtIsNull(String id);
}

