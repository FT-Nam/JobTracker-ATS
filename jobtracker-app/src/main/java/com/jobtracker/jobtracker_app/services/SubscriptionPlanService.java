package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.subscription.SubscriptionPlanCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.subscription.SubscriptionPlanUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.SubscriptionPlanResponse;

import java.util.List;

public interface SubscriptionPlanService {

    SubscriptionPlanResponse create(SubscriptionPlanCreationRequest request);

    SubscriptionPlanResponse getById(String id);

    List<SubscriptionPlanResponse> getAll();

    SubscriptionPlanResponse update(String id, SubscriptionPlanUpdateRequest request);

    void delete(String id);
}


