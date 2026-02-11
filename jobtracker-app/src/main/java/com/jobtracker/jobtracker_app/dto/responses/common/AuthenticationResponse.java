package com.jobtracker.jobtracker_app.dto.responses.common;

import com.jobtracker.jobtracker_app.dto.responses.TokenInfo;
import com.jobtracker.jobtracker_app.dto.responses.UserInfo;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    UserInfo user;
    TokenInfo tokens;
}
