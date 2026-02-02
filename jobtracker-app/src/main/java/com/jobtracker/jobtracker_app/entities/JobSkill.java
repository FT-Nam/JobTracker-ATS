package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.PartialAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity(name = "job_skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_job_skill", columnNames = {"job_id", "skill_id"}))
public class JobSkill extends PartialAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    Job job;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    Skill skill;

    @Column(name = "is_required", nullable = false)
    Boolean isRequired = true;

    @Column(name = "proficiency_level", length = 50)
    String proficiencyLevel;
}




