package com.jobtracker.jobtracker_app.dto.responses;

import java.util.Date;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenInfo {
    String accessToken;
    String refreshToken;
    Date expiresIn;
    Date refreshExpiresIn;
}
