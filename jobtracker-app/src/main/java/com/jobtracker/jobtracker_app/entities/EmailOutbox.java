package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.SystemAuditEntity;
import com.jobtracker.jobtracker_app.enums.EmailStatus;
import com.jobtracker.jobtracker_app.enums.EmailType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_outbox")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailOutbox extends SystemAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "email_type", nullable = false, length = 50)
    EmailType emailType;

    @Column(name = "aggregate_type", nullable = false, length = 50)
    String aggregateType;

    @Column(name = "aggregate_id", nullable = false, length = 36)
    String aggregateId;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    Company company;

    @Column(name = "to_email", nullable = false, length = 255)
    String toEmail;

    @Column(nullable = false, length = 500)
    String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    EmailStatus status = EmailStatus.PENDING;

    @Column(name = "retry_count", nullable = false)
    Integer retryCount = 0;

    @Column(name = "max_retries", nullable = false)
    Integer maxRetries = 3;

    @Column(name = "next_retry_at")
    LocalDateTime nextRetryAt;

    @Column(name = "sent_at")
    LocalDateTime sentAt;

    @Column(name = "failed_reason", columnDefinition = "TEXT")
    String failedReason;
}

