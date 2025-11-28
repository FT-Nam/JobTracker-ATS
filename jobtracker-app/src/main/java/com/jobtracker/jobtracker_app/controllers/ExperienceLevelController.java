package com.jobtracker.jobtracker_app.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.jobtracker.jobtracker_app.dto.requests.ExperienceLevelRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.ExperienceLevelResponse;
import com.jobtracker.jobtracker_app.dto.responses.PaginationInfo;
import com.jobtracker.jobtracker_app.services.ExperienceLevelService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/lookup/experience-levels")
public class ExperienceLevelController {
    ExperienceLevelService experienceLevelService;
    LocalizationUtils localizationUtils;

    @GetMapping
    public ApiResponse<List<ExperienceLevelResponse>> getAll(Pageable pageable) {
        Page<ExperienceLevelResponse> responses = experienceLevelService.getAll(pageable);
        return ApiResponse.<List<ExperienceLevelResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EXPERIENCE_LEVEL_LIST_SUCCESS))
                .data(responses.getContent())
                .paginationInfo(buildPagination(responses))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ExperienceLevelResponse> getById(@PathVariable String id) {
        return ApiResponse.<ExperienceLevelResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EXPERIENCE_LEVEL_DETAIL_SUCCESS))
                .data(experienceLevelService.getById(id))
                .build();
    }

    @PostMapping
    public ApiResponse<ExperienceLevelResponse> create(@RequestBody @Valid ExperienceLevelRequest request) {
        return ApiResponse.<ExperienceLevelResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EXPERIENCE_LEVEL_CREATE_SUCCESS))
                .data(experienceLevelService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ExperienceLevelResponse> update(
            @PathVariable String id, @RequestBody @Valid ExperienceLevelRequest request) {
        return ApiResponse.<ExperienceLevelResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EXPERIENCE_LEVEL_UPDATE_SUCCESS))
                .data(experienceLevelService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        experienceLevelService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EXPERIENCE_LEVEL_DELETE_SUCCESS))
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

