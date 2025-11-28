package com.jobtracker.jobtracker_app.entities;

import jakarta.persistence.*;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;

import lombok.*;

@Table(name = "experience_levels")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ExperienceLevel extends FullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, unique = true, length = 50)
    String name;

    @Column(name = "display_name", nullable = false, length = 100)
    String displayName;

    @Column(name = "min_years")
    Integer minYears = 0;

    @Column(name = "max_years")
    Integer maxYears;

    @Column(length = 255)
    String description;

    @Column(name = "is_active", nullable = false)
    Boolean isActive = true;
}

