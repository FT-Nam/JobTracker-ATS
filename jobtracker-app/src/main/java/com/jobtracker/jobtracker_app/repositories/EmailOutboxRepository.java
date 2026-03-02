package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.EmailOutbox;
import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmailOutboxRepository extends JpaRepository<EmailOutbox, String> {
    @Query("""
            SELECT e FROM EmailOutbox e
            WHERE e.status = 'PENDING'
            AND (e.nextRetryAt IS NULL OR e.nextRetryAt <= CURRENT_TIMESTAMP)
            AND (e.retryCount < e.maxRetries)
            ORDER BY e.createdAt ASC
            """)
    List<EmailOutbox> findPendingEmails(Pageable pageable);
}
