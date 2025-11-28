package com.jobtracker.jobtracker_app.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.jobtracker.jobtracker_app.dto.requests.InterviewStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.InterviewStatusResponse;
import com.jobtracker.jobtracker_app.dto.responses.PaginationInfo;
import com.jobtracker.jobtracker_app.services.InterviewStatusService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lookup/interview-statuses")
public class InterviewStatusController {
    InterviewStatusService interviewStatusService;
    LocalizationUtils localizationUtils;

    @GetMapping
    public ApiResponse<List<InterviewStatusResponse>> getAll(Pageable pageable) {
        Page<InterviewStatusResponse> responses = interviewStatusService.getAll(pageable);
        return ApiResponse.<List<InterviewStatusResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_STATUS_LIST_SUCCESS))
                .data(responses.getContent())
                .paginationInfo(buildPagination(responses))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<InterviewStatusResponse> getById(@PathVariable String id) {
        return ApiResponse.<InterviewStatusResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_STATUS_DETAIL_SUCCESS))
                .data(interviewStatusService.getById(id))
                .build();
    }

    @PostMapping
    public ApiResponse<InterviewStatusResponse> create(@RequestBody @Valid InterviewStatusRequest request) {
        return ApiResponse.<InterviewStatusResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_STATUS_CREATE_SUCCESS))
                .data(interviewStatusService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<InterviewStatusResponse> update(
            @PathVariable String id, @RequestBody @Valid InterviewStatusRequest request) {
        return ApiResponse.<InterviewStatusResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_STATUS_UPDATE_SUCCESS))
                .data(interviewStatusService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        interviewStatusService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_STATUS_DELETE_SUCCESS))
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

