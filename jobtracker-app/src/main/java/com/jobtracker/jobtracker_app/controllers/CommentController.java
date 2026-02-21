package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.CommentCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.CommentUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.CommentResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.PaginationInfo;
import com.jobtracker.jobtracker_app.services.CommentService;
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
@RequestMapping("/applications/{applicationId}/comments")
public class CommentController {

    CommentService commentService;
    LocalizationUtils localizationUtils;

    @GetMapping
    public ApiResponse<List<CommentResponse>> getComments(
            @PathVariable String applicationId,
            @RequestParam(required = false) Boolean isInternal,
            Pageable pageable) {
        Page<CommentResponse> comments = commentService.getComments(applicationId, isInternal, pageable);
        return ApiResponse.<List<CommentResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.COMMENT_LIST_SUCCESS))
                .data(comments.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(comments.getNumber())
                        .size(comments.getSize())
                        .totalElements(comments.getTotalElements())
                        .totalPages(comments.getTotalPages())
                        .build())
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CommentResponse> create(
            @PathVariable String applicationId,
            @RequestBody @Valid CommentCreationRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.COMMENT_CREATE_SUCCESS))
                .data(commentService.create(applicationId, request))
                .build();
    }

    @PutMapping("/{commentId}")
    public ApiResponse<CommentResponse> update(
            @PathVariable String applicationId,
            @PathVariable String commentId,
            @RequestBody @Valid CommentUpdateRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.COMMENT_UPDATE_SUCCESS))
                .data(commentService.update(applicationId, commentId, request))
                .build();
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> delete(
            @PathVariable String applicationId,
            @PathVariable String commentId) {
        commentService.delete(applicationId, commentId);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.COMMENT_DELETE_SUCCESS))
                .build();
    }
}
