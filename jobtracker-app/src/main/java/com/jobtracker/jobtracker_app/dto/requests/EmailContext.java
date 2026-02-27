package com.jobtracker.jobtracker_app.dto.requests;

import com.jobtracker.jobtracker_app.entities.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailContext {
    Company company;
    User user;
    Job job;
    Application application;
    Interview interview;
    ApplicationStatus applicationStatus;
    CompanySubscription companySubscription;
    SubscriptionPlan subscriptionPlan;
    Payment payment;

}

