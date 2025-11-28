package com.jobtracker.jobtracker_app.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.jobtracker.jobtracker_app.dto.requests.PriorityRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.PaginationInfo;
import com.jobtracker.jobtracker_app.dto.responses.PriorityResponse;
import com.jobtracker.jobtracker_app.services.PriorityService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lookup/priorities")
public class PriorityController {
    PriorityService priorityService;
    LocalizationUtils localizationUtils;

    @GetMapping
    public ApiResponse<List<PriorityResponse>> getAll(Pageable pageable) {
        Page<PriorityResponse> responses = priorityService.getAll(pageable);
        return ApiResponse.<List<PriorityResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PRIORITY_LIST_SUCCESS))
                .data(responses.getContent())
                .paginationInfo(buildPagination(responses))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<PriorityResponse> getById(@PathVariable String id) {
        return ApiResponse.<PriorityResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PRIORITY_DETAIL_SUCCESS))
                .data(priorityService.getById(id))
                .build();
    }

    @PostMapping
    public ApiResponse<PriorityResponse> create(@RequestBody @Valid PriorityRequest request) {
        return ApiResponse.<PriorityResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PRIORITY_CREATE_SUCCESS))
                .data(priorityService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<PriorityResponse> update(@PathVariable String id, @RequestBody @Valid PriorityRequest request) {
        return ApiResponse.<PriorityResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PRIORITY_UPDATE_SUCCESS))
                .data(priorityService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        priorityService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PRIORITY_DELETE_SUCCESS))
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

