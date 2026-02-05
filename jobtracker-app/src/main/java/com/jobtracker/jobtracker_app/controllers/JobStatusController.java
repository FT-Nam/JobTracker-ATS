package com.jobtracker.jobtracker_app.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.jobtracker.jobtracker_app.dto.requests.job.JobStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.JobStatusResponse;
import com.jobtracker.jobtracker_app.dto.responses.PaginationInfo;
import com.jobtracker.jobtracker_app.services.JobStatusService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lookup/job-statuses")
public class JobStatusController {
    JobStatusService jobStatusService;
    LocalizationUtils localizationUtils;

    @GetMapping
    public ApiResponse<List<JobStatusResponse>> getAll(Pageable pageable) {
        Page<JobStatusResponse> responses = jobStatusService.getAll(pageable);
        return ApiResponse.<List<JobStatusResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_STATUS_LIST_SUCCESS))
                .data(responses.getContent())
                .paginationInfo(buildPagination(responses))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<JobStatusResponse> getById(@PathVariable String id) {
        return ApiResponse.<JobStatusResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_STATUS_DETAIL_SUCCESS))
                .data(jobStatusService.getById(id))
                .build();
    }

    @PostMapping
    public ApiResponse<JobStatusResponse> create(@RequestBody @Valid JobStatusRequest request) {
        return ApiResponse.<JobStatusResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_STATUS_CREATE_SUCCESS))
                .data(jobStatusService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<JobStatusResponse> update(@PathVariable String id, @RequestBody @Valid JobStatusRequest request) {
        return ApiResponse.<JobStatusResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_STATUS_UPDATE_SUCCESS))
                .data(jobStatusService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        jobStatusService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_STATUS_DELETE_SUCCESS))
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

