package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.interview.InterviewCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.interview.InterviewUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.InterviewResponse;
import com.jobtracker.jobtracker_app.services.InterviewService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping
public class InterviewController {
    InterviewService interviewService;
    LocalizationUtils localizationUtils;

    @GetMapping("/applications/{applicationId}/interviews")
    public ApiResponse<List<InterviewResponse>> getByApplication(@PathVariable String applicationId) {
        return ApiResponse.<List<InterviewResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_LIST_SUCCESS))
                .data(interviewService.getAll(applicationId))
                .build();
    }

    @PostMapping("/applications/{applicationId}/interviews")
    public ApiResponse<InterviewResponse> create(
            @PathVariable String applicationId,
            @RequestBody @Valid InterviewCreationRequest request) {
        return ApiResponse.<InterviewResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_CREATE_SUCCESS))
                .data(interviewService.create(request, applicationId))
                .build();
    }

    @GetMapping("/interviews/{id}")
    public ApiResponse<InterviewResponse> getById(@PathVariable String id) {
        return ApiResponse.<InterviewResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_DETAIL_SUCCESS))
                .data(interviewService.getById(id))
                .build();
    }

    @PutMapping("/interviews/{id}")
    public ApiResponse<InterviewResponse> update(
            @PathVariable String id,
            @RequestBody @Valid InterviewUpdateRequest request) {
        return ApiResponse.<InterviewResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_UPDATE_SUCCESS))
                .data(interviewService.update(id, request))
                .build();
    }

    @DeleteMapping("/interviews/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        interviewService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_DELETE_SUCCESS))
                .build();
    }
}





