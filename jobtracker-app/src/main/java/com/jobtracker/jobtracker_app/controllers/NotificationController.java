package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.NotificationRequest;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.NotificationResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.PaginationInfo;
import com.jobtracker.jobtracker_app.services.NotificationService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;
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

    @PostMapping
    public ApiResponse<NotificationResponse> create(@RequestBody @Valid NotificationRequest request) {
        return ApiResponse.<NotificationResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_CREATE_SUCCESS))
                .data(notificationService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getAll(Pageable pageable) {
        Page<NotificationResponse> notifications = notificationService.getAll(pageable);
        return ApiResponse.<List<NotificationResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_LIST_SUCCESS))
                .data(notifications.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(notifications.getNumber())
                        .size(notifications.getSize())
                        .totalElements(notifications.getTotalElements())
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

    @PutMapping("/{id}")
    public ApiResponse<NotificationResponse> update(@PathVariable String id, @RequestBody @Valid NotificationRequest request) {
        return ApiResponse.<NotificationResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_UPDATE_SUCCESS))
                .data(notificationService.update(id, request))
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





