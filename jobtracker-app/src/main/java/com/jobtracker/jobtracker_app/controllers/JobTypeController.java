package com.jobtracker.jobtracker_app.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.jobtracker.jobtracker_app.dto.requests.job.JobTypeRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.JobTypeResponse;
import com.jobtracker.jobtracker_app.dto.responses.PaginationInfo;
import com.jobtracker.jobtracker_app.services.JobTypeService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lookup/job-types")
public class JobTypeController {
    JobTypeService jobTypeService;
    LocalizationUtils localizationUtils;

    @GetMapping
    public ApiResponse<List<JobTypeResponse>> getAll(Pageable pageable) {
        Page<JobTypeResponse> responses = jobTypeService.getAll(pageable);
        return ApiResponse.<List<JobTypeResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_TYPE_LIST_SUCCESS))
                .data(responses.getContent())
                .paginationInfo(buildPagination(responses))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<JobTypeResponse> getById(@PathVariable String id) {
        return ApiResponse.<JobTypeResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_TYPE_DETAIL_SUCCESS))
                .data(jobTypeService.getById(id))
                .build();
    }

    @PostMapping
    public ApiResponse<JobTypeResponse> create(@RequestBody @Valid JobTypeRequest request) {
        return ApiResponse.<JobTypeResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_TYPE_CREATE_SUCCESS))
                .data(jobTypeService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<JobTypeResponse> update(@PathVariable String id, @RequestBody @Valid JobTypeRequest request) {
        return ApiResponse.<JobTypeResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_TYPE_UPDATE_SUCCESS))
                .data(jobTypeService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        jobTypeService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_TYPE_DELETE_SUCCESS))
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

