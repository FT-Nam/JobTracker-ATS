package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.SystemAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
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
    @JoinColumn(name = "job_id")
    Job job;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
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

    @ManyToOne
    @JoinColumn(name = "priority_id", nullable = false)
    NotificationPriority priority;

    @Column(columnDefinition = "JSON")
    String metadata;
}




