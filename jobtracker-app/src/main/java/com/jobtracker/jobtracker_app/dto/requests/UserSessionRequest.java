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
public class UserSessionRequest {
    @NotBlank(message = "user_session.user_id.not_blank")
    String userId;

    @NotBlank(message = "user_session.session_token.not_blank")
    @Size(max = 500, message = "user_session.session_token.size")
    String sessionToken;

    @NotBlank(message = "user_session.refresh_token.not_blank")
    @Size(max = 500, message = "user_session.refresh_token.size")
    String refreshToken;

    String deviceInfo;

    @Size(max = 45, message = "user_session.ip_address.size")
    String ipAddress;

    String userAgent;

    Boolean isActive;

    @NotNull(message = "user_session.expires_at.not_null")
    LocalDateTime expiresAt;

    LocalDateTime lastUsedAt;
}




