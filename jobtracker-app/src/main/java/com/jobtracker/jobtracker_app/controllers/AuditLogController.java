package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.AuditLogRequest;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.AuditLogResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.PaginationInfo;
import com.jobtracker.jobtracker_app.services.AuditLogService;
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
@RequestMapping("/audit-logs")
public class AuditLogController {
    AuditLogService auditLogService;
    LocalizationUtils localizationUtils;

    @PostMapping
    public ApiResponse<AuditLogResponse> create(@RequestBody @Valid AuditLogRequest request) {
        return ApiResponse.<AuditLogResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.AUDIT_LOG_CREATE_SUCCESS))
                .data(auditLogService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<AuditLogResponse>> getAll(Pageable pageable) {
        Page<AuditLogResponse> auditLogs = auditLogService.getAll(pageable);
        return ApiResponse.<List<AuditLogResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.AUDIT_LOG_LIST_SUCCESS))
                .data(auditLogs.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(auditLogs.getNumber())
                        .size(auditLogs.getSize())
                        .totalElements(auditLogs.getTotalElements())
                        .build())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<AuditLogResponse> getById(@PathVariable String id) {
        return ApiResponse.<AuditLogResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.AUDIT_LOG_DETAIL_SUCCESS))
                .data(auditLogService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<AuditLogResponse> update(@PathVariable String id, @RequestBody @Valid AuditLogRequest request) {
        return ApiResponse.<AuditLogResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.AUDIT_LOG_UPDATE_SUCCESS))
                .data(auditLogService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        auditLogService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.AUDIT_LOG_DELETE_SUCCESS))
                .build();
    }
}





