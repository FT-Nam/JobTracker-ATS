package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.ApplicationStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationStatusHistoryRepository extends JpaRepository<ApplicationStatusHistory, String> {

    List<ApplicationStatusHistory> findByApplication_IdOrderByCreatedAtDesc(String applicationId);
}

