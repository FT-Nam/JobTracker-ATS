package com.jobtracker.jobtracker_app.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.jobtracker.jobtracker_app.entity.base.FullAuditEntity;

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

    @ManyToMany
    @JoinTable(
            name = "roles_permissions",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id"))
    List<Permission> permissions = new ArrayList<>();

    @Column(name = "is_active")
    Boolean isActive = true;
}
