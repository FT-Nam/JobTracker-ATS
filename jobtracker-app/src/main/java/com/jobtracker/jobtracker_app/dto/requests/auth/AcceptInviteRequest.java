package com.jobtracker.jobtracker_app.dto.requests.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptInviteRequest {
    @NotBlank
    String token;

    @NotBlank
    String password;
}
