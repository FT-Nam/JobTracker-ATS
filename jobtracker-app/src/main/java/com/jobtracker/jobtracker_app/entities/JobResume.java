package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.PartialAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_job_resume", columnNames = {"job_id", "resume_id"}))
public class JobResume extends PartialAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    Job job;

    @ManyToOne
    @JoinColumn(name = "resume_id", nullable = false)
    Resume resume;

    @Column(name = "is_primary", nullable = false)
    Boolean isPrimary = true;
}




