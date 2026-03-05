package com.jobtracker.jobtracker_app.scheduler;

import com.jobtracker.jobtracker_app.entities.CompanySubscription;
import com.jobtracker.jobtracker_app.enums.SubscriptionStatus;
import com.jobtracker.jobtracker_app.repositories.CompanySubscriptionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PlanLimitScheduler {

    CompanySubscriptionRepository companySubscriptionRepository;

    // Chạy mỗi giờ
    @Scheduled(cron = "${scheduler.plan-limit.cron:0 0 * * * ?}")
    @Transactional
    public void expireSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        List<CompanySubscription> toExpire = companySubscriptionRepository
                .findByStatusAndEndDateIsNotNullAndEndDateBefore(SubscriptionStatus.ACTIVE, now);

        for (CompanySubscription subscription : toExpire) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            companySubscriptionRepository.save(subscription);
            log.info("Expired subscription id={} companyId={} plan={} endDate={}",
                    subscription.getId(),
                    subscription.getCompany().getId(),
                    subscription.getPlan().getCode(),
                    subscription.getEndDate());
        }

        if (!toExpire.isEmpty()) {
            log.info("PlanLimitScheduler: expired {} subscription(s)", toExpire.size());
        }
    }
}
