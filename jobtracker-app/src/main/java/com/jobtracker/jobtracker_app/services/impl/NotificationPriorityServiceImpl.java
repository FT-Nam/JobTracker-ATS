package com.jobtracker.jobtracker_app.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_app.dto.requests.NotificationPriorityRequest;
import com.jobtracker.jobtracker_app.dto.responses.NotificationPriorityResponse;
import com.jobtracker.jobtracker_app.entities.NotificationPriority;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.NotificationPriorityMapper;
import com.jobtracker.jobtracker_app.repositories.NotificationPriorityRepository;
import com.jobtracker.jobtracker_app.services.NotificationPriorityService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class NotificationPriorityServiceImpl implements NotificationPriorityService {
    NotificationPriorityRepository notificationPriorityRepository;
    NotificationPriorityMapper notificationPriorityMapper;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('NOTIFICATION_PRIORITY_CREATE')")
    public NotificationPriorityResponse create(NotificationPriorityRequest request) {
        validateNameUnique(request.getName(), null);
        NotificationPriority notificationPriority = notificationPriorityMapper.toNotificationPriority(request);
        return notificationPriorityMapper.toNotificationPriorityResponse(
                notificationPriorityRepository.save(notificationPriority));
    }

    @Override
    public NotificationPriorityResponse getById(String id) {
        return notificationPriorityMapper.toNotificationPriorityResponse(findById(id));
    }

    @Override
    public Page<NotificationPriorityResponse> getAll(Pageable pageable) {
        return notificationPriorityRepository.findAll(pageable).map(notificationPriorityMapper::toNotificationPriorityResponse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('NOTIFICATION_PRIORITY_UPDATE')")
    public NotificationPriorityResponse update(String id, NotificationPriorityRequest request) {
        NotificationPriority notificationPriority = findById(id);
        if (request.getName() != null && !request.getName().equalsIgnoreCase(notificationPriority.getName())) {
            validateNameUnique(request.getName(), id);
        }
        notificationPriorityMapper.updateNotificationPriority(notificationPriority, request);
        return notificationPriorityMapper.toNotificationPriorityResponse(
                notificationPriorityRepository.save(notificationPriority));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('NOTIFICATION_PRIORITY_DELETE')")
    public void delete(String id) {
        NotificationPriority notificationPriority = findById(id);
        notificationPriority.softDelete();
        notificationPriorityRepository.save(notificationPriority);
    }

    private NotificationPriority findById(String id) {
        return notificationPriorityRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_PRIORITY_NOT_EXISTED));
    }

    private void validateNameUnique(String name, String excludeId) {
        boolean exists = excludeId == null
                ? notificationPriorityRepository.existsByNameIgnoreCase(name)
                : notificationPriorityRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId);
        if (exists) {
            throw new AppException(ErrorCode.NAME_EXISTED);
        }
    }
}

