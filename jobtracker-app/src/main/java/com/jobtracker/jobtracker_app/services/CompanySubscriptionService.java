package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.CompanySubscriptionRequest;
import com.jobtracker.jobtracker_app.dto.responses.CompanySubscriptionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanySubscriptionService {

    CompanySubscriptionResponse create(CompanySubscriptionRequest request);

    CompanySubscriptionResponse getById(String id);

    Page<CompanySubscriptionResponse> getAll(Pageable pageable);

    Page<CompanySubscriptionResponse> getByCompany(String companyId, Pageable pageable);

    CompanySubscriptionResponse getActiveByCompany(String companyId);

    CompanySubscriptionResponse update(String id, CompanySubscriptionRequest request);
}


