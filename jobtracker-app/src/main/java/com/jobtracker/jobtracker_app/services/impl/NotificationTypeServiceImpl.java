package com.jobtracker.jobtracker_app.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_app.dto.requests.NotificationTypeRequest;
import com.jobtracker.jobtracker_app.dto.responses.NotificationTypeResponse;
import com.jobtracker.jobtracker_app.entities.NotificationType;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.NotificationTypeMapper;
import com.jobtracker.jobtracker_app.repositories.NotificationTypeRepository;
import com.jobtracker.jobtracker_app.services.NotificationTypeService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class NotificationTypeServiceImpl implements NotificationTypeService {
    NotificationTypeRepository notificationTypeRepository;
    NotificationTypeMapper notificationTypeMapper;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('NOTIFICATION_TYPE_CREATE')")
    public NotificationTypeResponse create(NotificationTypeRequest request) {
        validateNameUnique(request.getName(), null);
        NotificationType notificationType = notificationTypeMapper.toNotificationType(request);
        return notificationTypeMapper.toNotificationTypeResponse(notificationTypeRepository.save(notificationType));
    }

    @Override
    public NotificationTypeResponse getById(String id) {
        return notificationTypeMapper.toNotificationTypeResponse(findById(id));
    }

    @Override
    public Page<NotificationTypeResponse> getAll(Pageable pageable) {
        return notificationTypeRepository.findAll(pageable).map(notificationTypeMapper::toNotificationTypeResponse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('NOTIFICATION_TYPE_UPDATE')")
    public NotificationTypeResponse update(String id, NotificationTypeRequest request) {
        NotificationType notificationType = findById(id);
        if (request.getName() != null && !request.getName().equalsIgnoreCase(notificationType.getName())) {
            validateNameUnique(request.getName(), id);
        }
        notificationTypeMapper.updateNotificationType(notificationType, request);
        return notificationTypeMapper.toNotificationTypeResponse(notificationTypeRepository.save(notificationType));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('NOTIFICATION_TYPE_DELETE')")
    public void delete(String id) {
        NotificationType notificationType = findById(id);
        notificationType.softDelete();
        notificationTypeRepository.save(notificationType);
    }

    private NotificationType findById(String id) {
        return notificationTypeRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_TYPE_NOT_EXISTED));
    }

    private void validateNameUnique(String name, String excludeId) {
        boolean exists = excludeId == null
                ? notificationTypeRepository.existsByNameIgnoreCase(name)
                : notificationTypeRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId);
        if (exists) {
            throw new AppException(ErrorCode.NAME_EXISTED);
        }
    }
}

