package com.jobtracker.jobtracker_app.dto.requests;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionPlanRequest {

    @NotBlank
    @Size(max = 50)
    String code;

    @NotBlank
    @Size(max = 100)
    String name;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    BigDecimal price;

    @NotNull
    @Min(0)
    Integer durationDays;

    Integer maxJobs;

    Integer maxUsers;

    Integer maxApplications;

    Boolean isActive;
}


