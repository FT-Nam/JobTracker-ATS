package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, String> {
}




