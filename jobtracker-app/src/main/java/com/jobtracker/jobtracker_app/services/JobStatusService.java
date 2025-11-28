package com.jobtracker.jobtracker_app.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobtracker.jobtracker_app.dto.requests.JobStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.JobStatusResponse;

public interface JobStatusService {
    JobStatusResponse create(JobStatusRequest request);

    JobStatusResponse getById(String id);

    Page<JobStatusResponse> getAll(Pageable pageable);

    JobStatusResponse update(String id, JobStatusRequest request);

    void delete(String id);
}

