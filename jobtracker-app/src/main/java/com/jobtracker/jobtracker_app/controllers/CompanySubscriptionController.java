package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.CompanySubscriptionRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.CompanySubscriptionResponse;
import com.jobtracker.jobtracker_app.dto.responses.PaginationInfo;
import com.jobtracker.jobtracker_app.services.CompanySubscriptionService;
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
public class CompanySubscriptionController {

    CompanySubscriptionService companySubscriptionService;
    LocalizationUtils localizationUtils;

    @PostMapping("/admin/company-subscriptions")
    public ApiResponse<CompanySubscriptionResponse> create(@RequestBody @Valid CompanySubscriptionRequest request) {
        return ApiResponse.<CompanySubscriptionResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.COMPANY_SUBSCRIPTION_CREATE_SUCCESS))
                .data(companySubscriptionService.create(request))
                .build();
    }

    @GetMapping("/admin/company-subscriptions/{id}")
    public ApiResponse<CompanySubscriptionResponse> getById(@PathVariable String id) {
        return ApiResponse.<CompanySubscriptionResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.COMPANY_SUBSCRIPTION_DETAIL_SUCCESS))
                .data(companySubscriptionService.getById(id))
                .build();
    }

    @GetMapping("/admin/company-subscriptions")
    public ApiResponse<List<CompanySubscriptionResponse>> getAll(Pageable pageable) {
        Page<CompanySubscriptionResponse> responses = companySubscriptionService.getAll(pageable);
        return ApiResponse.<List<CompanySubscriptionResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.COMPANY_SUBSCRIPTION_LIST_SUCCESS))
                .data(responses.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(responses.getNumber())
                        .size(responses.getSize())
                        .totalElements(responses.getTotalElements())
                        .totalPages(responses.getTotalPages())
                        .build())
                .build();
    }

    @GetMapping("/companies/{companyId}/subscription")
    public ApiResponse<CompanySubscriptionResponse> getActiveByCompany(@PathVariable String companyId) {
        return ApiResponse.<CompanySubscriptionResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.COMPANY_SUBSCRIPTION_DETAIL_SUCCESS))
                .data(companySubscriptionService.getActiveByCompany(companyId))
                .build();
    }

    @GetMapping("/companies/{companyId}/subscriptions")
    public ApiResponse<List<CompanySubscriptionResponse>> getByCompany(@PathVariable String companyId,
                                                                       Pageable pageable) {
        Page<CompanySubscriptionResponse> responses = companySubscriptionService.getByCompany(companyId, pageable);
        return ApiResponse.<List<CompanySubscriptionResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.COMPANY_SUBSCRIPTION_LIST_SUCCESS))
                .data(responses.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(responses.getNumber())
                        .size(responses.getSize())
                        .totalElements(responses.getTotalElements())
                        .totalPages(responses.getTotalPages())
                        .build())
                .build();
    }

    @PutMapping("/admin/company-subscriptions/{id}")
    public ApiResponse<CompanySubscriptionResponse> update(@PathVariable String id,
                                                           @RequestBody @Valid CompanySubscriptionRequest request) {
        return ApiResponse.<CompanySubscriptionResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.COMPANY_SUBSCRIPTION_UPDATE_SUCCESS))
                .data(companySubscriptionService.update(id, request))
                .build();
    }
}


