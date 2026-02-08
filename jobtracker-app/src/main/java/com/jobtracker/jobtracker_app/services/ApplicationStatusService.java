package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.ApplicationStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApplicationStatusResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApplicationStatusService {
    ApplicationStatusResponse create(ApplicationStatusRequest request);

    ApplicationStatusResponse getById(String id);

    Page<ApplicationStatusResponse> getAll(Pageable pageable);

    ApplicationStatusResponse update(String id, ApplicationStatusRequest request);

    void delete(String id);
}

