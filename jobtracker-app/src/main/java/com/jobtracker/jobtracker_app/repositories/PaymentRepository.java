package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Payment;
import com.jobtracker.jobtracker_app.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    Page<Payment> findByCompany_Id(String companyId, Pageable pageable);

    Page<Payment> findByCompanySubscription_Id(String companySubscriptionId, Pageable pageable);

    Optional<Payment> findByTxnRef(String txnRef);

    Page<Payment> findByCompany_IdAndStatus(String companyId, PaymentStatus status, Pageable pageable);
}


