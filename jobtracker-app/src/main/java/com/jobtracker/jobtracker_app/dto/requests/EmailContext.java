package com.jobtracker.jobtracker_app.dto.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailContext {
    String applicationId;
    String companyId;
    String userId;
    String jobId;
    String interviewId;
    String applicationStatusId;
    String companySubscriptionId;
    String subscriptionPlanId;
    String paymentId;

    Map<String, Object> manualValues;
}
