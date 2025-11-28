package com.jobtracker.jobtracker_app.dto.responses;

import java.time.LocalDateTime;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationPriorityResponse {
    String id;
    String name;
    String displayName;
    Integer level;
    String color;
    String description;
    Boolean isActive;
    String createdBy;
    String updatedBy;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    LocalDateTime deletedAt;
}

