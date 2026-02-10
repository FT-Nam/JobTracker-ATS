package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.SubscriptionPlanRequest;
import com.jobtracker.jobtracker_app.dto.responses.SubscriptionPlanResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubscriptionPlanService {

    SubscriptionPlanResponse create(SubscriptionPlanRequest request);

    SubscriptionPlanResponse getById(String id);

    Page<SubscriptionPlanResponse> getAll(Pageable pageable);

    SubscriptionPlanResponse update(String id, SubscriptionPlanRequest request);

    void delete(String id);
}


