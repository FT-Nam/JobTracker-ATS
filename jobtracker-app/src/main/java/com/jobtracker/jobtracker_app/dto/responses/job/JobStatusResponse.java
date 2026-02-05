package com.jobtracker.jobtracker_app.dto.responses.job;

import java.time.LocalDateTime;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobStatusResponse {
    String id;
    String name;
    String displayName;
    String description;
    String color;
    Integer sortOrder;
    Boolean isActive;
    String createdBy;
    String updatedBy;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    LocalDateTime deletedAt;
}

