package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.InterviewRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.InterviewResponse;
import com.jobtracker.jobtracker_app.dto.responses.PaginationInfo;
import com.jobtracker.jobtracker_app.services.InterviewService;
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
@RequestMapping("/interviews")
public class InterviewController {
    InterviewService interviewService;
    LocalizationUtils localizationUtils;

    @PostMapping
    public ApiResponse<InterviewResponse> create(@RequestBody @Valid InterviewRequest request) {
        return ApiResponse.<InterviewResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_CREATE_SUCCESS))
                .data(interviewService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<InterviewResponse>> getAll(Pageable pageable) {
        Page<InterviewResponse> interviews = interviewService.getAll(pageable);
        return ApiResponse.<List<InterviewResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_LIST_SUCCESS))
                .data(interviews.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(interviews.getNumber())
                        .size(interviews.getSize())
                        .totalElements(interviews.getTotalElements())
                        .build())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<InterviewResponse> getById(@PathVariable String id) {
        return ApiResponse.<InterviewResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_DETAIL_SUCCESS))
                .data(interviewService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<InterviewResponse> update(@PathVariable String id, @RequestBody @Valid InterviewRequest request) {
        return ApiResponse.<InterviewResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_UPDATE_SUCCESS))
                .data(interviewService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        interviewService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_DELETE_SUCCESS))
                .build();
    }
}




