package com.jobtracker.jobtracker_app.dto.responses.company;

import com.jobtracker.jobtracker_app.dto.responses.auth.UserRegisterResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanySelfSignupResponse {
    CompanyRegisterResponse company;
    UserRegisterResponse user;
}
