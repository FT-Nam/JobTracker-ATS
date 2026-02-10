package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.PaymentRequest;
import com.jobtracker.jobtracker_app.dto.responses.PaymentResponse;
import com.jobtracker.jobtracker_app.entities.Company;
import com.jobtracker.jobtracker_app.entities.CompanySubscription;
import com.jobtracker.jobtracker_app.entities.Payment;
import com.jobtracker.jobtracker_app.enums.PaymentStatus;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.PaymentMapper;
import com.jobtracker.jobtracker_app.repositories.CompanyRepository;
import com.jobtracker.jobtracker_app.repositories.CompanySubscriptionRepository;
import com.jobtracker.jobtracker_app.repositories.PaymentRepository;
import com.jobtracker.jobtracker_app.services.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentServiceImpl implements PaymentService {

    PaymentRepository paymentRepository;
    CompanyRepository companyRepository;
    CompanySubscriptionRepository companySubscriptionRepository;
    PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse create(PaymentRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_EXISTED));

        CompanySubscription companySubscription = companySubscriptionRepository.findById(request.getCompanySubscriptionId())
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_SUBSCRIPTION_NOT_EXISTED));

        Payment payment = paymentMapper.toPayment(request);
        payment.setCompany(company);
        payment.setCompanySubscription(companySubscription);

        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        if (payment.getCurrency() == null) {
            payment.setCurrency("VND");
        }

        if (payment.getGateway() == null) {
            payment.setGateway("VNPAY");
        }

        if (payment.getTxnRef() == null) {
            payment.setTxnRef(generateTxnRef());
        }

        payment.setStatus(PaymentStatus.INIT);

        return paymentMapper.toPaymentResponse(paymentRepository.save(payment));
    }

    @Override
    public PaymentResponse getById(String id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));
        return paymentMapper.toPaymentResponse(payment);
    }

    @Override
    public Page<PaymentResponse> getAll(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .map(paymentMapper::toPaymentResponse);
    }

    @Override
    public Page<PaymentResponse> getByCompany(String companyId, Pageable pageable) {
        return paymentRepository.findByCompany_Id(companyId, pageable)
                .map(paymentMapper::toPaymentResponse);
    }

    @Override
    public Page<PaymentResponse> getByCompanySubscription(String companySubscriptionId, Pageable pageable) {
        return paymentRepository.findByCompanySubscription_Id(companySubscriptionId, pageable)
                .map(paymentMapper::toPaymentResponse);
    }

    @Override
    @Transactional
    public PaymentResponse markSuccess(String txnRef, String metadata) {
        Payment payment = paymentRepository.findByTxnRef(txnRef)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        if (metadata != null) {
            payment.setMetadata(metadata);
        }

        return paymentMapper.toPaymentResponse(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public PaymentResponse markFailed(String txnRef, String metadata) {
        Payment payment = paymentRepository.findByTxnRef(txnRef)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        payment.setStatus(PaymentStatus.FAILED);
        if (metadata != null) {
            payment.setMetadata(metadata);
        }

        return paymentMapper.toPaymentResponse(paymentRepository.save(payment));
    }

    private String generateTxnRef() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
    }
}


