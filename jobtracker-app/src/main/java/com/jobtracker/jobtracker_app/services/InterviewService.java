package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.InterviewRequest;
import com.jobtracker.jobtracker_app.dto.responses.InterviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InterviewService {
    InterviewResponse create(InterviewRequest request);
    InterviewResponse getById(String id);
    Page<InterviewResponse> getAll(Pageable pageable);
    InterviewResponse update(String id, InterviewRequest request);
    void delete(String id);
}




