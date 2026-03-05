package com.jobtracker.jobtracker_app.services;

public interface PlanLimitService {

    void enforceApplicationLimit(String companyId);

    void enforceJobLimit(String companyId);

    void enforceUserLimit(String companyId);
}
