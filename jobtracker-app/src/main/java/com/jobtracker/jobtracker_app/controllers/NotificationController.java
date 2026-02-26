package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.NotificationRequest;
import com.jobtracker.jobtracker_app.dto.responses.NotificationMarkAllReadResponse;
import com.jobtracker.jobtracker_app.dto.responses.NotificationMarkReadResponse;
import com.jobtracker.jobtracker_app.dto.responses.NotificationResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.PaginationInfo;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.enums.NotificationType;
import com.jobtracker.jobtracker_app.services.NotificationService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;
import com.jobtracker.jobtracker_app.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    NotificationService notificationService;
    LocalizationUtils localizationUtils;
    SecurityUtils securityUtils;

    @PostMapping
    public ApiResponse<NotificationResponse> create(@RequestBody @Valid NotificationRequest request) {
        return ApiResponse.<NotificationResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_CREATE_SUCCESS))
                .data(notificationService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getAll(
            @RequestParam(value = "isRead", required = false) Boolean isRead,
            @RequestParam(value = "type", required = false) NotificationType type,
            @RequestParam(value = "applicationId", required = false) String applicationId,
            Pageable pageable
    ) {
        User currentUser = securityUtils.getCurrentUser();
        Page<NotificationResponse> notifications = notificationService.getAll(
                currentUser.getId(),
                currentUser.getCompany().getId(),
                isRead,
                type,
                applicationId,
                pageable
        );

        return ApiResponse.<List<NotificationResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_LIST_SUCCESS))
                .data(notifications.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(notifications.getNumber())
                        .size(notifications.getSize())
                        .totalElements(notifications.getTotalElements())
                        .totalPages(notifications.getTotalPages())
                        .build())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<NotificationResponse> getById(@PathVariable String id) {
        return ApiResponse.<NotificationResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_DETAIL_SUCCESS))
                .data(notificationService.getById(id))
                .build();
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<NotificationMarkReadResponse> markAsRead(@PathVariable String id) {
        return ApiResponse.<NotificationMarkReadResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_MARK_READ_SUCCESS))
                .data(notificationService.markNotification(id))
                .build();
    }

    @PatchMapping("/read-all")
    public ApiResponse<NotificationMarkAllReadResponse> markAllAsRead() {
        return ApiResponse.<NotificationMarkAllReadResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_MARK_ALL_READ_SUCCESS))
                .data(notificationService.markAllNotification())
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        notificationService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_DELETE_SUCCESS))
                .build();
    }
}

