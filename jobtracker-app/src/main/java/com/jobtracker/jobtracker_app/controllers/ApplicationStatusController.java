package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.ApplicationStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.ApplicationStatusResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.PaginationInfo;
import com.jobtracker.jobtracker_app.services.ApplicationStatusService;
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
@RequestMapping("/admin/application-statuses")
public class ApplicationStatusController {
    ApplicationStatusService applicationStatusService;
    LocalizationUtils localizationUtils;

    @PostMapping
    public ApiResponse<ApplicationStatusResponse> create(@RequestBody @Valid ApplicationStatusRequest request) {
        return ApiResponse.<ApplicationStatusResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_STATUS_CREATE_SUCCESS))
                .data(applicationStatusService.create(request))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ApplicationStatusResponse> getById(@PathVariable String id) {
        return ApiResponse.<ApplicationStatusResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_STATUS_DETAIL_SUCCESS))
                .data(applicationStatusService.getById(id))
                .build();
    }

    @GetMapping
    public ApiResponse<List<ApplicationStatusResponse>> getAll(Pageable pageable) {
        Page<ApplicationStatusResponse> responses = applicationStatusService.getAll(pageable);
        return ApiResponse.<List<ApplicationStatusResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_STATUS_LIST_SUCCESS))
                .data(responses.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(responses.getNumber())
                        .size(responses.getSize())
                        .totalElements(responses.getTotalElements())
                        .totalPages(responses.getTotalPages())
                        .build())
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ApplicationStatusResponse> update(@PathVariable String id,
                                                         @RequestBody @Valid ApplicationStatusRequest request) {
        return ApiResponse.<ApplicationStatusResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_STATUS_UPDATE_SUCCESS))
                .data(applicationStatusService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        applicationStatusService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_STATUS_DELETE_SUCCESS))
                .build();
    }
}

