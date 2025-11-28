package com.jobtracker.jobtracker_app.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobtracker.jobtracker_app.dto.requests.InterviewTypeRequest;
import com.jobtracker.jobtracker_app.dto.responses.InterviewTypeResponse;

public interface InterviewTypeService {
    InterviewTypeResponse create(InterviewTypeRequest request);

    InterviewTypeResponse getById(String id);

    Page<InterviewTypeResponse> getAll(Pageable pageable);

    InterviewTypeResponse update(String id, InterviewTypeRequest request);

    void delete(String id);
}

