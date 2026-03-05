package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.entities.CompanySubscription;
import com.jobtracker.jobtracker_app.enums.SubscriptionStatus;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.repositories.ApplicationRepository;
import com.jobtracker.jobtracker_app.repositories.CompanySubscriptionRepository;
import com.jobtracker.jobtracker_app.repositories.JobRepository;
import com.jobtracker.jobtracker_app.repositories.UserRepository;
import com.jobtracker.jobtracker_app.services.PlanLimitService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlanLimitServiceImpl implements PlanLimitService {

    CompanySubscriptionRepository companySubscriptionRepository;
    ApplicationRepository applicationRepository;
    JobRepository jobRepository;
    UserRepository userRepository;

    @Override
    public void enforceApplicationLimit(String companyId) {
        CompanySubscription subscription = getActiveSubscription(companyId);
        Integer maxApplications = subscription.getPlan().getMaxApplications();
        if (maxApplications == null) {
            return;
        }
        long currentCount = applicationRepository.countByCompany_IdAndDeletedAtIsNull(companyId);
        if (currentCount >= maxApplications) {
            throw new AppException(ErrorCode.PLAN_LIMIT_APPLICATIONS_EXCEEDED);
        }
    }

    @Override
    public void enforceJobLimit(String companyId) {
        CompanySubscription subscription = getActiveSubscription(companyId);
        Integer maxJobs = subscription.getPlan().getMaxJobs();
        if (maxJobs == null) {
            return;
        }
        long currentCount = jobRepository.countByCompany_IdAndDeletedAtIsNull(companyId);
        if (currentCount >= maxJobs) {
            throw new AppException(ErrorCode.PLAN_LIMIT_JOBS_EXCEEDED);
        }
    }

    @Override
    public void enforceUserLimit(String companyId) {
        CompanySubscription subscription = getActiveSubscription(companyId);
        Integer maxUsers = subscription.getPlan().getMaxUsers();
        if (maxUsers == null) {
            return;
        }
        long currentCount = userRepository.countByCompany_IdAndIsBillableTrueAndDeletedAtIsNull(companyId);
        if (currentCount >= maxUsers) {
            throw new AppException(ErrorCode.PLAN_LIMIT_USERS_EXCEEDED);
        }
    }

    private CompanySubscription getActiveSubscription(String companyId) {
        return companySubscriptionRepository
                .findLatestSubscription(companyId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_SUBSCRIPTION_NOT_EXISTED));
    }
}
