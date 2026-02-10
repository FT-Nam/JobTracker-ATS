package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.CompanySubscription;
import com.jobtracker.jobtracker_app.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanySubscriptionRepository extends JpaRepository<CompanySubscription, String>, JpaSpecificationExecutor<CompanySubscription> {

    Optional<CompanySubscription> findFirstByCompany_IdAndStatusOrderByStartDateDesc(String companyId, SubscriptionStatus status);

    Page<CompanySubscription> findByCompany_Id(String companyId, Pageable pageable);
}


