package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.ApplicationStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApplicationStatusResponse;

import java.util.List;

public interface ApplicationStatusService {
    ApplicationStatusResponse create(ApplicationStatusRequest request);

    ApplicationStatusResponse getById(String id);

    List<ApplicationStatusResponse> getAll();

    ApplicationStatusResponse update(String id, ApplicationStatusRequest request);

    void delete(String id);
}

