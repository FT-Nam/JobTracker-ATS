package com.jobtracker.jobtracker_app.entities;

import jakarta.persistence.*;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permission extends FullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(length = 50, unique = true, nullable = false)
    String name;

    @Column(length = 100, nullable = false)
    String resource;

    @Column(length = 50, nullable = false)
    String action;

    String description;

    @Column(name = "is_active")
    Boolean isActive = true;
}
