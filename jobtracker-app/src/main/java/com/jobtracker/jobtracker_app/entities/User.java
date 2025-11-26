package com.jobtracker.jobtracker_app.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends FullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, unique = true)
    String email;

    String password;

    @Column(name = "first_name", nullable = false, length = 100)
    String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    String lastName;

    @Column(length = 20)
    String phone;

    @Column(name = "avatar_url", length = 500)
    String avatarUrl;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    Role role;

    @Column(name = "is_active")
    Boolean isActive = true;

    @Column(name = "email_verified")
    Boolean emailVerified = false;

    @Column(name = "google_id", unique = true, length = 100)
    String googleId;

    @Column(name = "last_login_at")
    LocalDateTime lastLoginAt;
}
