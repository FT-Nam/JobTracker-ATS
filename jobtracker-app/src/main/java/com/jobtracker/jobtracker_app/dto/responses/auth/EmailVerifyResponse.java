package com.jobtracker.jobtracker_app.dto.responses.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailVerifyResponse {
    String email;
    Boolean emailVerified;
}
