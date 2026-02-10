package com.jobtracker.jobtracker_app.dto.requests;

import com.jobtracker.jobtracker_app.enums.SubscriptionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanySubscriptionRequest {

    @NotBlank
    String companyId;

    @NotBlank
    String planId;

    @NotNull
    LocalDateTime startDate;

    LocalDateTime endDate;

    SubscriptionStatus status;
}


