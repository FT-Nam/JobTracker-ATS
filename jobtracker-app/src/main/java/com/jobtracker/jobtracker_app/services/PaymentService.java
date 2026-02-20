package com.jobtracker.jobtracker_app.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jobtracker.jobtracker_app.dto.requests.PaymentRequest;
import com.jobtracker.jobtracker_app.dto.responses.payment.InitPaymentResponse;
import com.jobtracker.jobtracker_app.dto.responses.payment.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.UnsupportedEncodingException;

public interface PaymentService {

    InitPaymentResponse create(PaymentRequest request, HttpServletRequest httpServletRequest)
            throws UnsupportedEncodingException;

    boolean paymentReturn(HttpServletRequest request) throws UnsupportedEncodingException, JsonProcessingException;

    PaymentResponse getById(String id);

    Page<PaymentResponse> getAll(Pageable pageable);

    Page<PaymentResponse> getByCompany(String companyId, Pageable pageable);

    Page<PaymentResponse> getByCompanySubscription(String companySubscriptionId, Pageable pageable);
}


