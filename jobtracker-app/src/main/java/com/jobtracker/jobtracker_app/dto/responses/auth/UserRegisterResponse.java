package com.jobtracker.jobtracker_app.dto.responses.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRegisterResponse {
    String id;
    String email;
    String firstName;
    String lastName;
    String roleName;
    Boolean emailVerified;
    Boolean isActive;
}
