package com.jobtracker.jobtracker_app.dto.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResendEmailVerifyRequest {
    String email;
}
