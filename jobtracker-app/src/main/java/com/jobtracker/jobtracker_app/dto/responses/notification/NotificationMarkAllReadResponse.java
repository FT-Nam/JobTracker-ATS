package com.jobtracker.jobtracker_app.dto.responses.notification;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationMarkAllReadResponse {
    int updateCount;
}
