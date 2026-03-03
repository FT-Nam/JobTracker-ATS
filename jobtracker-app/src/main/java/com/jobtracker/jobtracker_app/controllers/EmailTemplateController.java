package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.email.*;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.PaginationInfo;
import com.jobtracker.jobtracker_app.dto.responses.email.*;
import com.jobtracker.jobtracker_app.services.EmailTemplateService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/admin/email-templates")
public class EmailTemplateController {
    EmailTemplateService emailTemplateService;
    LocalizationUtils localizationUtils;

    @GetMapping
    public ApiResponse<List<EmailTemplateResponse>> getAll(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            Pageable pageable
    ) {
        Page<EmailTemplateResponse> templates = emailTemplateService.getAll(code, name, isActive, pageable);
        return ApiResponse.<List<EmailTemplateResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EMAIL_TEMPLATE_LIST_SUCCESS))
                .data(templates.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(templates.getNumber())
                        .size(templates.getSize())
                        .totalElements(templates.getTotalElements())
                        .totalPages(templates.getTotalPages())
                        .build())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<EmailTemplateDetailResponse> getById(@PathVariable String id) {
        return ApiResponse.<EmailTemplateDetailResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EMAIL_TEMPLATE_DETAIL_SUCCESS))
                .data(emailTemplateService.getById(id))
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EmailTemplateResponse> create(@RequestBody @Valid EmailTemplateCreationRequest request) {
        return ApiResponse.<EmailTemplateResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EMAIL_TEMPLATE_CREATE_SUCCESS))
                .data(emailTemplateService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<EmailTemplateResponse> update(
            @PathVariable String id,
            @RequestBody @Valid EmailTemplateUpdateRequest request
    ) {
        return ApiResponse.<EmailTemplateResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EMAIL_TEMPLATE_UPDATE_SUCCESS))
                .data(emailTemplateService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        emailTemplateService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EMAIL_TEMPLATE_DELETE_SUCCESS))
                .build();
    }

    @PostMapping("/{id}/preview")
    public ApiResponse<EmailTemplatePreviewResponse> preview(
            @PathVariable String id,
            @RequestBody(required = false) EmailTemplatePreviewRequest request
    ) {
        return ApiResponse.<EmailTemplatePreviewResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EMAIL_TEMPLATE_PREVIEW_SUCCESS))
                .data(emailTemplateService.preview(id, request != null ? request : EmailTemplatePreviewRequest.builder().build()))
                .build();
    }

    @PostMapping("/{id}/send-test")
    public ApiResponse<Void> sendTest(
            @PathVariable String id,
            @RequestBody(required = false) EmailTemplateSendTestRequest request
    ) {
        emailTemplateService.sendTest(id, request);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EMAIL_TEMPLATE_SEND_TEST_SUCCESS))
                .build();
    }
}
