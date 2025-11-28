package com.jobtracker.jobtracker_app.dto.requests;

import jakarta.validation.constraints.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationPriorityRequest {
    @NotBlank(message = "notification_priority.name.not_blank")
    @Size(max = 50, message = "notification_priority.name.size")
    String name;

    @NotBlank(message = "notification_priority.display_name.not_blank")
    @Size(max = 100, message = "notification_priority.display_name.size")
    String displayName;

    @NotNull(message = "notification_priority.level.not_null")
    @Min(value = 1, message = "notification_priority.level.min")
    @Max(value = 5, message = "notification_priority.level.max")
    Integer level;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "notification_priority.color.invalid")
    String color;

    @Size(max = 255, message = "notification_priority.description.size")
    String description;

    Boolean isActive;
}

