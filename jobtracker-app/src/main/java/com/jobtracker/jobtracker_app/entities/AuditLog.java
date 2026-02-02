package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "entity_type", nullable = false, length = 100)
    String entityType;

    @Column(name = "entity_id", nullable = false, length = 36)
    String entityId;

    @Column(nullable = false, length = 50)
    String action;

    @Column(name = "old_values", columnDefinition = "JSON")
    String oldValues;

    @Column(name = "new_values", columnDefinition = "JSON")
    String newValues;

    @Column(name = "ip_address", length = 45)
    String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    String userAgent;
}




