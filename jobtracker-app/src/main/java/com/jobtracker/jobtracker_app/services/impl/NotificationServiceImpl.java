package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.NotificationRequest;
import com.jobtracker.jobtracker_app.dto.responses.NotificationMarkAllReadResponse;
import com.jobtracker.jobtracker_app.dto.responses.NotificationMarkReadResponse;
import com.jobtracker.jobtracker_app.dto.responses.NotificationResponse;
import com.jobtracker.jobtracker_app.entities.*;
import com.jobtracker.jobtracker_app.enums.NotificationType;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.NotificationMapper;
import com.jobtracker.jobtracker_app.repositories.*;
import com.jobtracker.jobtracker_app.services.NotificationService;
import com.jobtracker.jobtracker_app.utils.SecurityUtils;
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
    CompanyRepository companyRepository;
    JobRepository jobRepository;
    ApplicationRepository applicationRepository;
    SecurityUtils securityUtils;

    @Override
    @Transactional
    public NotificationResponse create(NotificationRequest request) {
        User user = userRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(request.getUserId(), request.getCompanyId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Company company = companyRepository
                .findByIdAndDeletedAtIsNull(request.getCompanyId())
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_EXISTED));

        Notification notification = notificationMapper.toNotification(request);

        notification.setUser(user);
        notification.setCompany(company);
        
        if (request.getJobId() != null) {
            Job job = jobRepository
                    .findByIdAndCompany_IdAndDeletedAtIsNull(request.getJobId(), request.getCompanyId())
                    .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED));

            notification.setJob(job);
        }
        if (request.getApplicationId() != null) {
            Application application = applicationRepository
                    .findByIdAndCompany_IdAndDeletedAtIsNull(request.getApplicationId(), request.getCompanyId())
                    .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));
            notification.setApplication(application);
        }
        
        return notificationMapper.toNotificationResponse(notificationRepository.save(notification));
    }

    @Override
    public NotificationResponse getById(String id) {
        Notification notification = getNotificationForCurrentCompanyUserOrThrow(id);

        return notificationMapper.toNotificationResponse(notification);
    }

    @Override
    public Page<NotificationResponse> getAll(String userId,
                                             String companyId,
                                             Boolean isRead,
                                             NotificationType type,
                                             String applicationId,
                                             Pageable pageable) {
        return notificationRepository
                .searchNotification(userId, companyId, isRead, type, applicationId, pageable)
                .map(notificationMapper::toNotificationResponse);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Notification notification = getNotificationForCurrentCompanyUserOrThrow(id);

        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public NotificationMarkReadResponse markNotification(String id) {
        Notification notification = getNotificationForCurrentCompanyUserOrThrow(id);

        notification.setIsRead(true);

        return NotificationMarkReadResponse.builder()
                .id(notification.getId())
                .isRead(notification.getIsRead())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public NotificationMarkAllReadResponse markAllNotification() {
        User user = securityUtils.getCurrentUser();
        int updateCount  = notificationRepository
                .markAllAsRead(user.getCompany().getId(), user.getId());

        return NotificationMarkAllReadResponse.builder()
                .updateCount(updateCount)
                .build();
    }

    private Notification getNotificationForCurrentCompanyUserOrThrow(String id){
        User user = securityUtils.getCurrentUser();

        return notificationRepository
                .findByIdAndCompany_IdAndUser_Id(id, user.getCompany().getId(), user.getId())
                .orElseThrow(()-> new AppException(ErrorCode.NOTIFICATION_NOT_EXISTED));
    }
}




