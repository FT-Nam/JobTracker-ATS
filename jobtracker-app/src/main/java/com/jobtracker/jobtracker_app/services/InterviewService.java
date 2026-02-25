package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.interview.InterviewCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.interview.InterviewUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.InterviewResponse;

import java.util.List;

public interface InterviewService {
    InterviewResponse create(InterviewCreationRequest request, String applicationId);
    InterviewResponse getById(String id);
    List<InterviewResponse> getAll(String applicationId);
    InterviewResponse update(String id, InterviewUpdateRequest request);
    void delete(String id);
    void cancel(String id);
}





