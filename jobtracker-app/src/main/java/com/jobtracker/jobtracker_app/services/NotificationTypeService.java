package com.jobtracker.jobtracker_app.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobtracker.jobtracker_app.dto.requests.NotificationTypeRequest;
import com.jobtracker.jobtracker_app.dto.responses.NotificationTypeResponse;

public interface NotificationTypeService {
    NotificationTypeResponse create(NotificationTypeRequest request);

    NotificationTypeResponse getById(String id);

    Page<NotificationTypeResponse> getAll(Pageable pageable);

    NotificationTypeResponse update(String id, NotificationTypeRequest request);

    void delete(String id);
}

