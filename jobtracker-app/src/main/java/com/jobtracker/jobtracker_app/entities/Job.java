package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "jobs")
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

    @ManyToOne
    @JoinColumn(name = "job_type_id", nullable = false)
    JobType jobType;

    String location;

    @Column(name = "salary_min", precision = 12, scale = 2)
    BigDecimal salaryMin;

    @Column(name = "salary_max", precision = 12, scale = 2)
    BigDecimal salaryMax;

    @Column(length = 3)
    String currency = "VND";

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    JobStatus status;

    @Column(name = "application_date")
    LocalDate applicationDate;

    @Column(name = "deadline_date")
    LocalDate deadlineDate;

    @Column(name = "interview_date")
    LocalDate interviewDate;

    @Column(name = "offer_date")
    LocalDate offerDate;

    @Column(name = "job_description", columnDefinition = "TEXT")
    String jobDescription;

    @Column(columnDefinition = "TEXT")
    String requirements;

    @Column(columnDefinition = "TEXT")
    String benefits;

    @Column(name = "job_url", length = 500)
    String jobUrl;

    @Column(columnDefinition = "TEXT")
    String notes;

    @ManyToOne
    @JoinColumn(name = "priority_id", nullable = false)
    Priority priority;

    @Column(name = "is_remote", nullable = false)
    Boolean isRemote = false;

    @ManyToOne
    @JoinColumn(name = "experience_level_id")
    ExperienceLevel experienceLevel;
}




