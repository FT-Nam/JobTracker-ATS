package com.jobtracker.jobtracker_app.entities;

import jakarta.persistence.*;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
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

    @Column(nullable = false)
    String name;

    @Column(length = 500)
    String website;

    @Column(length = 100)
    String industry;

    @Column(length = 50)
    String size;

    String location;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(length = 500)
    String logoUrl;

    @Column(nullable = false)
    Boolean isVerified = false;
}
