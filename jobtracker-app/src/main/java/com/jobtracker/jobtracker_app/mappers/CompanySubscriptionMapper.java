package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.subscription.CompanySubscriptionRequest;
import com.jobtracker.jobtracker_app.dto.responses.subscription.CompanySubscriptionResponse;
import com.jobtracker.jobtracker_app.entities.CompanySubscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanySubscriptionMapper {

    CompanySubscription toCompanySubscription(CompanySubscriptionRequest request);

    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "plan.id", target = "planId")
    @Mapping(source = "plan.code", target = "planCode")
    @Mapping(source = "plan.name", target = "planName")
    CompanySubscriptionResponse toCompanySubscriptionResponse(CompanySubscription companySubscription);
}


