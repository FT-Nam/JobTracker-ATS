package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;
import com.jobtracker.jobtracker_app.enums.JobStatus;
import com.jobtracker.jobtracker_app.enums.JobType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Job extends FullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    Company company;

    @Column(nullable = false, length = 255)
    String title;

    @Column(nullable = false, length = 255)
    String position;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false, length = 50)
    JobType jobType;

    String location;

    @Column(name = "salary_min", precision = 12, scale = 2)
    BigDecimal salaryMin;

    @Column(name = "salary_max", precision = 12, scale = 2)
    BigDecimal salaryMax;

    @Column(length = 3)
    String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(name = "job_status", nullable = false, length = 50)
    JobStatus jobStatus = JobStatus.DRAFT;

    @Column(name = "deadline_date")
    LocalDate deadlineDate;

    @Column(name = "job_description", columnDefinition = "TEXT")
    String jobDescription;

    @Column(columnDefinition = "TEXT")
    String requirements;

    @Column(columnDefinition = "TEXT")
    String benefits;

    @Column(name = "job_url", length = 500)
    String jobUrl;

    @Column(name = "is_remote", nullable = false)
    Boolean isRemote = false;

    @Column(name = "published_at")
    LocalDateTime publishedAt;

    @Column(name = "expires_at")
    LocalDateTime expiresAt;

    @Column(name = "views_count", nullable = false)
    Integer viewsCount = 0;

    @Column(name = "applications_count", nullable = false)
    Integer applicationsCount = 0;
}




