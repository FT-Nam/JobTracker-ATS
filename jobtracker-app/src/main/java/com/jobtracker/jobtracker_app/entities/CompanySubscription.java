package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.SystemAuditEntity;
import com.jobtracker.jobtracker_app.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "company_subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanySubscription extends SystemAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    Company company;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    SubscriptionPlan plan;

    @Column(name = "start_date", nullable = false)
    LocalDateTime startDate;

    @Column(name = "end_date")
    LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    SubscriptionStatus status;
}


