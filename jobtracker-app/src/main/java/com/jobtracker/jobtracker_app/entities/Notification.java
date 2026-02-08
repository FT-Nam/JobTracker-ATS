package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.SystemAuditEntity;
import com.jobtracker.jobtracker_app.enums.NotificationType;
import com.jobtracker.jobtracker_app.enums.NotificationPriority;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification extends SystemAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    Company company;

    @ManyToOne
    @JoinColumn(name = "job_id")
    Job job;

    @ManyToOne
    @JoinColumn(name = "application_id")
    Application application;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    NotificationType type;

    @Column(nullable = false, length = 255)
    String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    String message;

    @Column(name = "is_read", nullable = false)
    Boolean isRead = false;

    @Column(name = "is_sent", nullable = false)
    Boolean isSent = false;

    @Column(name = "sent_at")
    LocalDateTime sentAt;

    @Column(name = "scheduled_at")
    LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 50)
    NotificationPriority priority;

    @Column(columnDefinition = "JSON")
    String metadata;
}




