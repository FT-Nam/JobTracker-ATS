package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.ResumeRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.PaginationInfo;
import com.jobtracker.jobtracker_app.dto.responses.ResumeResponse;
import com.jobtracker.jobtracker_app.services.ResumeService;
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
@RequestMapping("/resumes")
public class ResumeController {
    ResumeService resumeService;
    LocalizationUtils localizationUtils;

    @PostMapping
    public ApiResponse<ResumeResponse> create(@RequestBody @Valid ResumeRequest request) {
        return ApiResponse.<ResumeResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.RESUME_CREATE_SUCCESS))
                .data(resumeService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<ResumeResponse>> getAll(Pageable pageable) {
        Page<ResumeResponse> resumes = resumeService.getAll(pageable);
        return ApiResponse.<List<ResumeResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.RESUME_LIST_SUCCESS))
                .data(resumes.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(resumes.getNumber())
                        .size(resumes.getSize())
                        .totalElements(resumes.getTotalElements())
                        .build())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ResumeResponse> getById(@PathVariable String id) {
        return ApiResponse.<ResumeResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.RESUME_DETAIL_SUCCESS))
                .data(resumeService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ResumeResponse> update(@PathVariable String id, @RequestBody @Valid ResumeRequest request) {
        return ApiResponse.<ResumeResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.RESUME_UPDATE_SUCCESS))
                .data(resumeService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        resumeService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.RESUME_DELETE_SUCCESS))
                .build();
    }
}




