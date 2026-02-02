package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.AttachmentRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.AttachmentResponse;
import com.jobtracker.jobtracker_app.dto.responses.PaginationInfo;
import com.jobtracker.jobtracker_app.services.AttachmentService;
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
@RequestMapping("/attachments")
public class AttachmentController {
    AttachmentService attachmentService;
    LocalizationUtils localizationUtils;

    @PostMapping
    public ApiResponse<AttachmentResponse> create(@RequestBody @Valid AttachmentRequest request) {
        return ApiResponse.<AttachmentResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ATTACHMENT_CREATE_SUCCESS))
                .data(attachmentService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<AttachmentResponse>> getAll(Pageable pageable) {
        Page<AttachmentResponse> attachments = attachmentService.getAll(pageable);
        return ApiResponse.<List<AttachmentResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ATTACHMENT_LIST_SUCCESS))
                .data(attachments.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(attachments.getNumber())
                        .size(attachments.getSize())
                        .totalElements(attachments.getTotalElements())
                        .build())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<AttachmentResponse> getById(@PathVariable String id) {
        return ApiResponse.<AttachmentResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ATTACHMENT_DETAIL_SUCCESS))
                .data(attachmentService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<AttachmentResponse> update(@PathVariable String id, @RequestBody @Valid AttachmentRequest request) {
        return ApiResponse.<AttachmentResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ATTACHMENT_UPDATE_SUCCESS))
                .data(attachmentService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        attachmentService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ATTACHMENT_DELETE_SUCCESS))
                .build();
    }
}




