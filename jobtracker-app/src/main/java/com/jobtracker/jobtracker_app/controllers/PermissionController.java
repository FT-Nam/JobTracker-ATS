package com.jobtracker.jobtracker_app.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.jobtracker.jobtracker_app.dto.requests.PermissionRequest;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.PaginationInfo;
import com.jobtracker.jobtracker_app.dto.responses.PermissionResponse;
import com.jobtracker.jobtracker_app.services.PermissionService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/permission")
public class PermissionController {
    PermissionService permissionService;
    LocalizationUtils localizationUtils;

    @PostMapping
    public ApiResponse<PermissionResponse> create(@RequestBody @Valid PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PERMISSION_CREATE_SUCCESS))
                .data(permissionService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<PermissionResponse>> getAll(Pageable pageable) {
        Page<PermissionResponse> permissions = permissionService.getAll(pageable);
        return ApiResponse.<List<PermissionResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PERMISSION_LIST_SUCCESS))
                .data(permissions.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(permissions.getNumber())
                        .size(permissions.getSize())
                        .totalElements(permissions.getTotalElements())
                        .build())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<PermissionResponse> getById(@PathVariable String id) {
        return ApiResponse.<PermissionResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PERMISSION_DETAIL_SUCCESS))
                .data(permissionService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<PermissionResponse> update(
            @PathVariable String id, @RequestBody @Valid PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PERMISSION_UPDATE_SUCCESS))
                .data(permissionService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        permissionService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PERMISSION_DELETE_SUCCESS))
                .build();
    }
}
