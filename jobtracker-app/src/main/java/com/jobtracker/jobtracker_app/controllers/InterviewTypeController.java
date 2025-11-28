package com.jobtracker.jobtracker_app.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.jobtracker.jobtracker_app.dto.requests.InterviewTypeRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.InterviewTypeResponse;
import com.jobtracker.jobtracker_app.dto.responses.PaginationInfo;
import com.jobtracker.jobtracker_app.services.InterviewTypeService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lookup/interview-types")
public class InterviewTypeController {
    InterviewTypeService interviewTypeService;
    LocalizationUtils localizationUtils;

    @GetMapping
    public ApiResponse<List<InterviewTypeResponse>> getAll(Pageable pageable) {
        Page<InterviewTypeResponse> responses = interviewTypeService.getAll(pageable);
        return ApiResponse.<List<InterviewTypeResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_TYPE_LIST_SUCCESS))
                .data(responses.getContent())
                .paginationInfo(buildPagination(responses))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<InterviewTypeResponse> getById(@PathVariable String id) {
        return ApiResponse.<InterviewTypeResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_TYPE_DETAIL_SUCCESS))
                .data(interviewTypeService.getById(id))
                .build();
    }

    @PostMapping
    public ApiResponse<InterviewTypeResponse> create(@RequestBody @Valid InterviewTypeRequest request) {
        return ApiResponse.<InterviewTypeResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_TYPE_CREATE_SUCCESS))
                .data(interviewTypeService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<InterviewTypeResponse> update(
            @PathVariable String id, @RequestBody @Valid InterviewTypeRequest request) {
        return ApiResponse.<InterviewTypeResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_TYPE_UPDATE_SUCCESS))
                .data(interviewTypeService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        interviewTypeService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_TYPE_DELETE_SUCCESS))
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

