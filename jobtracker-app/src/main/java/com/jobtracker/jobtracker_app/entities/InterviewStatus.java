package com.jobtracker.jobtracker_app.entities;

import jakarta.persistence.*;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;

import lombok.*;

@Table(name = "interview_statuses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class InterviewStatus extends FullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, unique = true, length = 50)
    String name;

    @Column(name = "display_name", nullable = false, length = 100)
    String displayName;

    @Column(length = 255)
    String description;

    @Column(length = 7)
    String color = "#6B7280";

    @Column(name = "is_active", nullable = false)
    Boolean isActive = true;
}

