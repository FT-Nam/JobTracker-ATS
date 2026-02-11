package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.SubscriptionPlanRequest;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.PaginationInfo;
import com.jobtracker.jobtracker_app.dto.responses.SubscriptionPlanResponse;
import com.jobtracker.jobtracker_app.services.SubscriptionPlanService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/admin/subscription-plans")
public class SubscriptionPlanController {

    SubscriptionPlanService subscriptionPlanService;
    LocalizationUtils localizationUtils;

    @PostMapping
    public ApiResponse<SubscriptionPlanResponse> create(@RequestBody @Valid SubscriptionPlanRequest request) {
        return ApiResponse.<SubscriptionPlanResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.SUBSCRIPTION_PLAN_CREATE_SUCCESS))
                .data(subscriptionPlanService.create(request))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<SubscriptionPlanResponse> getById(@PathVariable String id) {
        return ApiResponse.<SubscriptionPlanResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.SUBSCRIPTION_PLAN_DETAIL_SUCCESS))
                .data(subscriptionPlanService.getById(id))
                .build();
    }

    @GetMapping
    public ApiResponse<List<SubscriptionPlanResponse>> getAll(Pageable pageable) {
        Page<SubscriptionPlanResponse> responses = subscriptionPlanService.getAll(pageable);
        return ApiResponse.<List<SubscriptionPlanResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.SUBSCRIPTION_PLAN_LIST_SUCCESS))
                .data(responses.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(responses.getNumber())
                        .size(responses.getSize())
                        .totalElements(responses.getTotalElements())
                        .totalPages(responses.getTotalPages())
                        .build())
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<SubscriptionPlanResponse> update(@PathVariable String id,
                                                        @RequestBody @Valid SubscriptionPlanRequest request) {
        return ApiResponse.<SubscriptionPlanResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.SUBSCRIPTION_PLAN_UPDATE_SUCCESS))
                .data(subscriptionPlanService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        subscriptionPlanService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.SUBSCRIPTION_PLAN_DELETE_SUCCESS))
                .build();
    }
}


