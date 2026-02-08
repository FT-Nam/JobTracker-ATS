package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;
import com.jobtracker.jobtracker_app.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Application extends FullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    Job job;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    Company company;

    @Column(name = "candidate_name", nullable = false, length = 255)
    String candidateName;

    @Column(name = "candidate_email", nullable = false, length = 255)
    String candidateEmail;

    @Column(name = "candidate_phone", length = 20)
    String candidatePhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    ApplicationStatus status = ApplicationStatus.NEW;

    @Column(length = 100)
    String source;

    @Column(name = "applied_date", nullable = false)
    LocalDate appliedDate;

    @Column(name = "resume_file_path", length = 500)
    String resumeFilePath;

    @Column(name = "cover_letter", columnDefinition = "TEXT")
    String coverLetter;

    @Column(columnDefinition = "TEXT")
    String notes;

    @Column
    Integer rating;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    User assignedTo;
}

