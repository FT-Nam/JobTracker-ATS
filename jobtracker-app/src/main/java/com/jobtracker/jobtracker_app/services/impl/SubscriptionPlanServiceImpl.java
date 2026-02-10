package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.SubscriptionPlanRequest;
import com.jobtracker.jobtracker_app.dto.responses.SubscriptionPlanResponse;
import com.jobtracker.jobtracker_app.entities.SubscriptionPlan;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.SubscriptionPlanMapper;
import com.jobtracker.jobtracker_app.repositories.SubscriptionPlanRepository;
import com.jobtracker.jobtracker_app.services.SubscriptionPlanService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    SubscriptionPlanRepository subscriptionPlanRepository;
    SubscriptionPlanMapper subscriptionPlanMapper;

    @Override
    @Transactional
    public SubscriptionPlanResponse create(SubscriptionPlanRequest request) {
        subscriptionPlanRepository.findByCodeIgnoreCase(request.getCode())
                .ifPresent(existing -> {
                    throw new AppException(ErrorCode.FIELD_EXISTED);
                });

        SubscriptionPlan plan = subscriptionPlanMapper.toSubscriptionPlan(request);
        return subscriptionPlanMapper.toSubscriptionPlanResponse(subscriptionPlanRepository.save(plan));
    }

    @Override
    public SubscriptionPlanResponse getById(String id) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_PLAN_NOT_EXISTED));
        return subscriptionPlanMapper.toSubscriptionPlanResponse(plan);
    }

    @Override
    public Page<SubscriptionPlanResponse> getAll(Pageable pageable) {
        return subscriptionPlanRepository.findAll(pageable)
                .map(subscriptionPlanMapper::toSubscriptionPlanResponse);
    }

    @Override
    @Transactional
    public SubscriptionPlanResponse update(String id, SubscriptionPlanRequest request) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_PLAN_NOT_EXISTED));

        if (request.getCode() != null && !request.getCode().equalsIgnoreCase(plan.getCode())) {
            subscriptionPlanRepository.findByCodeIgnoreCase(request.getCode())
                    .ifPresent(existing -> {
                        throw new AppException(ErrorCode.FIELD_EXISTED);
                    });
        }

        subscriptionPlanMapper.updateSubscriptionPlan(plan, request);
        return subscriptionPlanMapper.toSubscriptionPlanResponse(subscriptionPlanRepository.save(plan));
    }

    @Override
    @Transactional
    public void delete(String id) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_PLAN_NOT_EXISTED));
        plan.setIsActive(false);
        subscriptionPlanRepository.save(plan);
    }
}


