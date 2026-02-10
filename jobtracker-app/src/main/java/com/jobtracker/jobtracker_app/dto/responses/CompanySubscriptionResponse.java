package com.jobtracker.jobtracker_app.dto.responses;

import com.jobtracker.jobtracker_app.enums.SubscriptionStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanySubscriptionResponse {

    String id;
    String companyId;
    String planId;
    String planCode;
    String planName;
    SubscriptionStatus status;
    LocalDateTime startDate;
    LocalDateTime endDate;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}


