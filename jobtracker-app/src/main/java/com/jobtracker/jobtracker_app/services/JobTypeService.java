package com.jobtracker.jobtracker_app.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobtracker.jobtracker_app.dto.requests.JobTypeRequest;
import com.jobtracker.jobtracker_app.dto.responses.JobTypeResponse;

public interface JobTypeService {
    JobTypeResponse create(JobTypeRequest request);

    JobTypeResponse getById(String id);

    Page<JobTypeResponse> getAll(Pageable pageable);

    JobTypeResponse update(String id, JobTypeRequest request);

    void delete(String id);
}

