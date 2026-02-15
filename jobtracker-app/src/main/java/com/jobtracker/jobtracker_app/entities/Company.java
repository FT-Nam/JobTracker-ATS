package com.jobtracker.jobtracker_app.entities;

import jakarta.persistence.*;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Company extends FullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, length = 255)
    String name;

    @Column(length = 500)
    String website;

    @Column(length = 100)
    String industry;

    @Column(length = 50)
    String size;

    @Column(length = 255)
    String location;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(name = "logo_url", length = 500)
    String logoUrl;

    @Column(name = "is_verified", nullable = false)
    Boolean isVerified = false;

    @Column(name = "is_active", nullable = false)
    Boolean isActive = true;
}
