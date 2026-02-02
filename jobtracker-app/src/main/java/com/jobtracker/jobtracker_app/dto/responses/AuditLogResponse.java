package com.jobtracker.jobtracker_app.dto.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditLogResponse {
    String id;
    String userId;
    String entityType;
    String entityId;
    String action;
    String oldValues;
    String newValues;
    String ipAddress;
    String userAgent;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}




