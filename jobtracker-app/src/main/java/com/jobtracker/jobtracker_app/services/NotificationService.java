package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.NotificationRequest;
import com.jobtracker.jobtracker_app.dto.responses.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    NotificationResponse create(NotificationRequest request);
    NotificationResponse getById(String id);
    Page<NotificationResponse> getAll(Pageable pageable);
    NotificationResponse update(String id, NotificationRequest request);
    void delete(String id);
}





