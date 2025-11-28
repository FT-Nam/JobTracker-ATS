package com.jobtracker.jobtracker_app.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.jobtracker.jobtracker_app.dto.requests.NotificationTypeRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.NotificationTypeResponse;
import com.jobtracker.jobtracker_app.dto.responses.PaginationInfo;
import com.jobtracker.jobtracker_app.services.NotificationTypeService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lookup/notification-types")
public class NotificationTypeController {
    NotificationTypeService notificationTypeService;
    LocalizationUtils localizationUtils;

    @GetMapping
    public ApiResponse<List<NotificationTypeResponse>> getAll(Pageable pageable) {
        Page<NotificationTypeResponse> responses = notificationTypeService.getAll(pageable);
        return ApiResponse.<List<NotificationTypeResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_TYPE_LIST_SUCCESS))
                .data(responses.getContent())
                .paginationInfo(buildPagination(responses))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<NotificationTypeResponse> getById(@PathVariable String id) {
        return ApiResponse.<NotificationTypeResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_TYPE_DETAIL_SUCCESS))
                .data(notificationTypeService.getById(id))
                .build();
    }

    @PostMapping
    public ApiResponse<NotificationTypeResponse> create(@RequestBody @Valid NotificationTypeRequest request) {
        return ApiResponse.<NotificationTypeResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_TYPE_CREATE_SUCCESS))
                .data(notificationTypeService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<NotificationTypeResponse> update(
            @PathVariable String id, @RequestBody @Valid NotificationTypeRequest request) {
        return ApiResponse.<NotificationTypeResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_TYPE_UPDATE_SUCCESS))
                .data(notificationTypeService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        notificationTypeService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.NOTIFICATION_TYPE_DELETE_SUCCESS))
                .build();
    }

    private PaginationInfo buildPagination(Page<?> page) {
        return PaginationInfo.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}

