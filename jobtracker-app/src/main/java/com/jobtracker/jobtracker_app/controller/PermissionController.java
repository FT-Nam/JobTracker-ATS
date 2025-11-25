package com.jobtracker.jobtracker_app.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.jobtracker.jobtracker_app.dto.request.PermissionRequest;
import com.jobtracker.jobtracker_app.dto.response.ApiResponse;
import com.jobtracker.jobtracker_app.dto.response.PaginationInfo;
import com.jobtracker.jobtracker_app.dto.response.PermissionResponse;
import com.jobtracker.jobtracker_app.serivce.PermissionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/permission")
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    public ApiResponse<PermissionResponse> create(@RequestBody @Valid PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .message("Permission create successfully")
                .data(permissionService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<PermissionResponse>> getAll(Pageable pageable) {
        Page<PermissionResponse> permissions = permissionService.getAll(pageable);
        return ApiResponse.<List<PermissionResponse>>builder()
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
                .data(permissionService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<PermissionResponse> update(
            @PathVariable String id, @RequestBody @Valid PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .message("Permission update successfully")
                .data(permissionService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        permissionService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Permission delete successfully")
                .build();
    }
}
