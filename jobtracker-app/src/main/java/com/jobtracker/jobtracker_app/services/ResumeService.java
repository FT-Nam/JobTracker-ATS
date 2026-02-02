package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.ResumeRequest;
import com.jobtracker.jobtracker_app.dto.responses.ResumeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResumeService {
    ResumeResponse create(ResumeRequest request);
    ResumeResponse getById(String id);
    Page<ResumeResponse> getAll(Pageable pageable);
    ResumeResponse update(String id, ResumeRequest request);
    void delete(String id);
}




