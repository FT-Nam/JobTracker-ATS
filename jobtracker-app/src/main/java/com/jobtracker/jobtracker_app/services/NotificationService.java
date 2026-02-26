package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.NotificationRequest;
import com.jobtracker.jobtracker_app.dto.responses.NotificationMarkAllReadResponse;
import com.jobtracker.jobtracker_app.dto.responses.NotificationMarkReadResponse;
import com.jobtracker.jobtracker_app.dto.responses.NotificationResponse;
import com.jobtracker.jobtracker_app.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    NotificationResponse create(NotificationRequest request);
    NotificationResponse getById(String id);
    Page<NotificationResponse> getAll(String userId,
                                      String companyId,
                                      Boolean isRead,
                                      NotificationType type,
                                      String applicationId,
                                      Pageable pageable);
    void delete(String id);
    NotificationMarkReadResponse markNotification(String id);
    NotificationMarkAllReadResponse markAllNotification();
}





