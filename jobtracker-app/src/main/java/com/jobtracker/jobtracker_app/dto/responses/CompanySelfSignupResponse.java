package com.jobtracker.jobtracker_app.dto.responses;

import com.jobtracker.jobtracker_app.dto.responses.user.UserResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Response for Company Self-Signup (POST /auth/register).
 * Contains the created company (id, name) and the admin user.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanySelfSignupResponse {
    CompanyResponse company;
    UserResponse user;
}
