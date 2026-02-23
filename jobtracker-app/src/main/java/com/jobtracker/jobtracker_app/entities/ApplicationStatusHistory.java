package com.jobtracker.jobtracker_app.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "application_status_history")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    Application application;

    @ManyToOne
    @JoinColumn(name = "from_status_id")
    ApplicationStatus fromStatus;

    @ManyToOne
    @JoinColumn(name = "to_status_id", nullable = false)
    ApplicationStatus toStatus;

    @ManyToOne
    @JoinColumn(name = "changed_by", nullable = false)
    User changedBy;

    @Column(columnDefinition = "TEXT")
    String notes;

    @Column(name = "created_at")
    @CreatedDate
    LocalDateTime createdAt;
}

