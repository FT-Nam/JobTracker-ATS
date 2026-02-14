package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.PartialAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "interview_interviewers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InterviewInterviewer extends PartialAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "interview_id", nullable = false)
    Interview interview;

    @ManyToOne
    @JoinColumn(name = "interviewer_id", nullable = false)
    User interviewer;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    Company company;

    @Column(name = "is_primary")
    Boolean isPrimary = false;
}

