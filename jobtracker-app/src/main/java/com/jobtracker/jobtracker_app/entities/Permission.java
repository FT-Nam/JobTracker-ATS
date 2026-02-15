package com.jobtracker.jobtracker_app.entities;

import jakarta.persistence.*;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

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

    @Column(length = 100, unique = true, nullable = false)
    String name;

    @Column(length = 100, nullable = false)
    String resource;

    @Column(length = 50, nullable = false)
    String action;

    @Column(length = 255)
    String description;

    @Column(name = "is_active")
    Boolean isActive = true;

    @OneToMany(mappedBy = "permission")
    List<RolePermission> rolePermissions = new ArrayList<>();
}
