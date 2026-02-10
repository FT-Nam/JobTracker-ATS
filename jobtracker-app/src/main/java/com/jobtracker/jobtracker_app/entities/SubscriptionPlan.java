package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.SystemAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "subscription_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionPlan extends SystemAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, length = 50, unique = true)
    String code;

    @Column(nullable = false, length = 100)
    String name;

    @Column(nullable = false, precision = 10, scale = 2)
    BigDecimal price;

    @Column(name = "duration_days", nullable = false)
    Integer durationDays;

    @Column(name = "max_jobs")
    Integer maxJobs;

    @Column(name = "max_users")
    Integer maxUsers;

    @Column(name = "max_applications")
    Integer maxApplications;

    @Column(name = "is_active", nullable = false)
    Boolean isActive = true;
}


