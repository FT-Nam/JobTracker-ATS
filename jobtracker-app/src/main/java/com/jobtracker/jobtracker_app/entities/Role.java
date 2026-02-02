package com.jobtracker.jobtracker_app.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role extends FullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(length = 50, unique = true, nullable = false)
    String name;

    String description;

    @OneToMany(mappedBy = "role")
    List<User> users;

    @OneToMany(mappedBy = "role")
    List<RolePermission> rolePermissions = new ArrayList<>();

    @Column(name = "is_active")
    Boolean isActive = true;
}
