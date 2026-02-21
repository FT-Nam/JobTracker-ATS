package com.jobtracker.jobtracker_app.dto.requests.company;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyVerifyRequest {
    @NotNull(message = "{company.isVerified.not_null}")
    Boolean isVerified;
}
