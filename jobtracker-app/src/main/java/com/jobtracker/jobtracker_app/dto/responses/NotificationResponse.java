package com.jobtracker.jobtracker_app.dto.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    String id;
    String userId;
    String jobId;
    String typeId;
    String title;
    String message;
    Boolean isRead;
    Boolean isSent;
    LocalDateTime sentAt;
    LocalDateTime scheduledAt;
    String priorityId;
    String metadata;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}




