package com.jobtracker.jobtracker_app.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobtracker.jobtracker_app.dto.requests.PriorityRequest;
import com.jobtracker.jobtracker_app.dto.responses.PriorityResponse;

public interface PriorityService {
    PriorityResponse create(PriorityRequest request);

    PriorityResponse getById(String id);

    Page<PriorityResponse> getAll(Pageable pageable);

    PriorityResponse update(String id, PriorityRequest request);

    void delete(String id);
}

