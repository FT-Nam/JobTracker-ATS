package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.NotificationRequest;
import com.jobtracker.jobtracker_app.dto.responses.NotificationResponse;
import com.jobtracker.jobtracker_app.entities.Notification;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.NotificationMapper;
import com.jobtracker.jobtracker_app.repositories.*;
import com.jobtracker.jobtracker_app.services.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {
    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;
    UserRepository userRepository;
    JobRepository jobRepository;
    NotificationTypeRepository notificationTypeRepository;
    NotificationPriorityRepository notificationPriorityRepository;

    @Override
    @Transactional
    public NotificationResponse create(NotificationRequest request) {
        Notification notification = notificationMapper.toNotification(request);
        notification.setUser(userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        notification.setType(notificationTypeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_TYPE_NOT_EXISTED)));
        notification.setPriority(notificationPriorityRepository.findById(request.getPriorityId())
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_PRIORITY_NOT_EXISTED)));
        
        if (request.getJobId() != null) {
            notification.setJob(jobRepository.findById(request.getJobId())
                    .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED)));
        }
        
        return notificationMapper.toNotificationResponse(notificationRepository.save(notification));
    }

    @Override
    public NotificationResponse getById(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_EXISTED));
        return notificationMapper.toNotificationResponse(notification);
    }

    @Override
    public Page<NotificationResponse> getAll(Pageable pageable) {
        return notificationRepository.findAll(pageable).map(notificationMapper::toNotificationResponse);
    }

    @Override
    @Transactional
    public NotificationResponse update(String id, NotificationRequest request) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_EXISTED));

        notificationMapper.updateNotification(notification, request);
        
        if (request.getUserId() != null) {
            notification.setUser(userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        }
        if (request.getTypeId() != null) {
            notification.setType(notificationTypeRepository.findById(request.getTypeId())
                    .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_TYPE_NOT_EXISTED)));
        }
        if (request.getPriorityId() != null) {
            notification.setPriority(notificationPriorityRepository.findById(request.getPriorityId())
                    .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_PRIORITY_NOT_EXISTED)));
        }
        if (request.getJobId() != null) {
            notification.setJob(jobRepository.findById(request.getJobId())
                    .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED)));
        }

        return notificationMapper.toNotificationResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_EXISTED));
        notificationRepository.delete(notification);
    }
}




