package com.jobtracker.jobtracker_app.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobtracker.jobtracker_app.dto.requests.NotificationPriorityRequest;
import com.jobtracker.jobtracker_app.dto.responses.NotificationPriorityResponse;

public interface NotificationPriorityService {
    NotificationPriorityResponse create(NotificationPriorityRequest request);

    NotificationPriorityResponse getById(String id);

    Page<NotificationPriorityResponse> getAll(Pageable pageable);

    NotificationPriorityResponse update(String id, NotificationPriorityRequest request);

    void delete(String id);
}

