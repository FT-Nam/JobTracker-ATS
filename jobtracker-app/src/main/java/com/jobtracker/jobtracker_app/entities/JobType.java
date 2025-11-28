package com.jobtracker.jobtracker_app.entities;

import jakarta.persistence.*;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;

import lombok.*;

@Table(name = "job_types")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class JobType extends FullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, unique = true, length = 50)
    String name;

    @Column(name = "display_name", nullable = false, length = 100)
    String displayName;

    @Column(length = 255)
    String description;

    @Column(name = "is_active", nullable = false)
    Boolean isActive = true;
}

