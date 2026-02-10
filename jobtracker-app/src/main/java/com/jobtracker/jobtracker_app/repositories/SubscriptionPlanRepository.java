package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, String>, JpaSpecificationExecutor<SubscriptionPlan> {

    Optional<SubscriptionPlan> findByCodeIgnoreCase(String code);
}


