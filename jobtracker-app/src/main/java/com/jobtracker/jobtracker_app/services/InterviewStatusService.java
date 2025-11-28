package com.jobtracker.jobtracker_app.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobtracker.jobtracker_app.dto.requests.InterviewStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.InterviewStatusResponse;

public interface InterviewStatusService {
    InterviewStatusResponse create(InterviewStatusRequest request);

    InterviewStatusResponse getById(String id);

    Page<InterviewStatusResponse> getAll(Pageable pageable);

    InterviewStatusResponse update(String id, InterviewStatusRequest request);

    void delete(String id);
}

