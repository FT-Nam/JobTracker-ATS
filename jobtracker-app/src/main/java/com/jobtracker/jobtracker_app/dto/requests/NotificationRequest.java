package com.jobtracker.jobtracker_app.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationRequest {
    @NotBlank(message = "notification.user_id.not_blank")
    String userId;

    String jobId;

    @NotBlank(message = "notification.type_id.not_blank")
    String typeId;

    @NotBlank(message = "notification.title.not_blank")
    @Size(max = 255, message = "notification.title.size")
    String title;

    @NotBlank(message = "notification.message.not_blank")
    String message;

    Boolean isRead;

    Boolean isSent;

    LocalDateTime sentAt;

    LocalDateTime scheduledAt;

    @NotBlank(message = "notification.priority_id.not_blank")
    String priorityId;

    String metadata;
}




