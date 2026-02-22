package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.application.ApplyToJobRequest;

import java.io.IOException;

public interface ApplicationService {
    void ApplyToJob(ApplyToJobRequest request, String jobId) throws IOException;
}
