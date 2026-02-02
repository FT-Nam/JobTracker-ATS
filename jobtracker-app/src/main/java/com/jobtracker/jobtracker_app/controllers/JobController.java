package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.JobRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.JobResponse;
import com.jobtracker.jobtracker_app.dto.responses.PaginationInfo;
import com.jobtracker.jobtracker_app.services.JobService;
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
@RequestMapping("/jobs")
public class JobController {
    JobService jobService;
    LocalizationUtils localizationUtils;

    @PostMapping
    public ApiResponse<JobResponse> create(@RequestBody @Valid JobRequest request) {
        return ApiResponse.<JobResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_CREATE_SUCCESS))
                .data(jobService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<JobResponse>> getAll(Pageable pageable) {
        Page<JobResponse> jobs = jobService.getAll(pageable);
        return ApiResponse.<List<JobResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_LIST_SUCCESS))
                .data(jobs.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(jobs.getNumber())
                        .size(jobs.getSize())
                        .totalElements(jobs.getTotalElements())
                        .build())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<JobResponse> getById(@PathVariable String id) {
        return ApiResponse.<JobResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_DETAIL_SUCCESS))
                .data(jobService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<JobResponse> update(@PathVariable String id, @RequestBody @Valid JobRequest request) {
        return ApiResponse.<JobResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_UPDATE_SUCCESS))
                .data(jobService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        jobService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_DELETE_SUCCESS))
                .build();
    }
}




