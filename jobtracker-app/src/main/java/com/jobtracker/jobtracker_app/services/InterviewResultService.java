package com.jobtracker.jobtracker_app.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobtracker.jobtracker_app.dto.requests.InterviewResultRequest;
import com.jobtracker.jobtracker_app.dto.responses.InterviewResultResponse;

public interface InterviewResultService {
    InterviewResultResponse create(InterviewResultRequest request);

    InterviewResultResponse getById(String id);

    Page<InterviewResultResponse> getAll(Pageable pageable);

    InterviewResultResponse update(String id, InterviewResultRequest request);

    void delete(String id);
}

