package com.jobtracker.jobtracker_app.dto.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSessionResponse {
    String id;
    String userId;
    String sessionToken;
    String refreshToken;
    String deviceInfo;
    String ipAddress;
    String userAgent;
    Boolean isActive;
    LocalDateTime expiresAt;
    LocalDateTime lastUsedAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}




