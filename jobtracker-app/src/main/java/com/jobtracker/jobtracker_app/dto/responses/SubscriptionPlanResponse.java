package com.jobtracker.jobtracker_app.dto.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionPlanResponse {

    String id;
    String code;
    String name;
    BigDecimal price;
    Integer durationDays;
    Integer maxJobs;
    Integer maxUsers;
    Integer maxApplications;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}


