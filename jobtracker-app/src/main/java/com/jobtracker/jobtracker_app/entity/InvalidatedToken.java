package com.jobtracker.jobtracker_app.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.jobtracker.jobtracker_app.entity.base.FullAuditEntity;

import lombok.*;

@Table(name = "invalidated_token")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class InvalidatedToken extends FullAuditEntity {
    @Id
    String id;

    Date expiryTime;
}
