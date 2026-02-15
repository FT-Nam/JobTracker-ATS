package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.SystemAuditEntity;
import com.jobtracker.jobtracker_app.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment extends SystemAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    Company company;

    @ManyToOne
    @JoinColumn(name = "company_subscription_id", nullable = false)
    CompanySubscription companySubscription;

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal amount;

    @Column(length = 3)
    String currency = "VND";

    @Column(nullable = false, length = 50)
    String gateway;

    @Column(name = "txn_ref", nullable = false, length = 100, unique = true)
    String txnRef;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    PaymentStatus status;

    @Column(name = "paid_at")
    LocalDateTime paidAt;

    @Column(columnDefinition = "JSON")
    String metadata;
}


