package com.jobtracker.jobtracker_app.entities;

import com.jobtracker.jobtracker_app.entities.base.FullAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "invalidated_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvalidatedToken extends FullAuditEntity {
    @Id
    @Column(length = 255)
    String id;

    @Column(name = "expiry_time", nullable = false)
    LocalDateTime expiryTime;
}
