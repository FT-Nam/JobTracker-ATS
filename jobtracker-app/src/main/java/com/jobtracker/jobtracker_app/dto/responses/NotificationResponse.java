package com.jobtracker.jobtracker_app.dto.responses;

import com.jobtracker.jobtracker_app.enums.NotificationType;
import com.jobtracker.jobtracker_app.enums.NotificationPriority;
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
    String companyId;
    String jobId;
    String applicationId;
    NotificationType type;
    String title;
    String message;
    Boolean isRead;
    Boolean isSent;
    LocalDateTime sentAt;
    LocalDateTime scheduledAt;
    NotificationPriority priority;
    String metadata;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}




