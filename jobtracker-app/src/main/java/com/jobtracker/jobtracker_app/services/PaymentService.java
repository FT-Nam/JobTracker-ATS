package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.PaymentRequest;
import com.jobtracker.jobtracker_app.dto.responses.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    PaymentResponse create(PaymentRequest request);

    PaymentResponse getById(String id);

    Page<PaymentResponse> getAll(Pageable pageable);

    Page<PaymentResponse> getByCompany(String companyId, Pageable pageable);

    Page<PaymentResponse> getByCompanySubscription(String companySubscriptionId, Pageable pageable);

    PaymentResponse markSuccess(String txnRef, String metadata);

    PaymentResponse markFailed(String txnRef, String metadata);
}


