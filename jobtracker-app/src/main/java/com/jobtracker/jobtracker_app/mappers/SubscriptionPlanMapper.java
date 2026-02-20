package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.subscription.SubscriptionPlanCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.subscription.SubscriptionPlanUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.SubscriptionPlanResponse;
import com.jobtracker.jobtracker_app.entities.SubscriptionPlan;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SubscriptionPlanMapper {

    SubscriptionPlan toSubscriptionPlan(SubscriptionPlanCreationRequest request);

    SubscriptionPlanResponse toSubscriptionPlanResponse(SubscriptionPlan subscriptionPlan);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSubscriptionPlan(@MappingTarget SubscriptionPlan subscriptionPlan, SubscriptionPlanUpdateRequest request);
}


