package com.jobtracker.jobtracker_app.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.jobtracker.jobtracker_app.dto.requests.RoleCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.RolePermissionRequest;
import com.jobtracker.jobtracker_app.dto.requests.RolePermissionsRequest;
import com.jobtracker.jobtracker_app.dto.requests.RoleUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.PaginationInfo;
import com.jobtracker.jobtracker_app.dto.responses.RolePermissionsResponse;
import com.jobtracker.jobtracker_app.dto.responses.RolePermissionsUpdateResponse;
import com.jobtracker.jobtracker_app.dto.responses.RoleResponse;
import com.jobtracker.jobtracker_app.services.RoleService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/admin/roles")
public class RoleController {
    RoleService roleService;
    LocalizationUtils localizationUtils;

    @PostMapping
    public ApiResponse<RoleResponse> create(@RequestBody @Valid RoleCreationRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_CREATE_SUCCESS))
                .data(roleService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAll(Pageable pageable) {
        Page<RoleResponse> responses = roleService.getAll(pageable);
        return ApiResponse.<List<RoleResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_LIST_SUCCESS))
                .data(responses.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(responses.getNumber())
                        .size(responses.getSize())
                        .totalElements(responses.getTotalElements())
                        .build())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleResponse> getById(@PathVariable String id) {
        return ApiResponse.<RoleResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DETAIL_SUCCESS))
                .data(roleService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleResponse> update(@PathVariable String id, @RequestBody @Valid RoleUpdateRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_UPDATE_SUCCESS))
                .data(roleService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        roleService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DELETE_SUCCESS))
                .build();
    }

    @GetMapping("/{roleId}/permissions")
    public ApiResponse<List<RolePermissionsResponse>> getRolePermissions(
            @PathVariable String roleId, Pageable pageable) {
        Page<RolePermissionsResponse> responses = roleService.getRolePermissions(roleId, pageable);
        return ApiResponse.<List<RolePermissionsResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_PERMISSIONS_RETRIEVED_SUCCESS))
                .data(responses.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(responses.getNumber())
                        .size(responses.getSize())
                        .totalElements(responses.getTotalElements())
                        .build())
                .build();
    }

    @PutMapping("/{roleId}/permissions")
    public ApiResponse<RolePermissionsUpdateResponse> updateRolePermissions(
            @PathVariable String roleId, @RequestBody @Valid RolePermissionsRequest request) {
        return ApiResponse.<RolePermissionsUpdateResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_PERMISSIONS_UPDATE_SUCCESS))
                .data(roleService.updateRolePermissions(roleId, request))
                .build();
    }

    @PostMapping("/{roleId}/permissions")
    public ApiResponse<Void> addPermissionToRole(
            @PathVariable String roleId, @RequestBody @Valid RolePermissionRequest request) {
        roleService.addPermissionToRole(roleId, request);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_PERMISSION_ADD_SUCCESS))
                .build();
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ApiResponse<Void> removePermissionFromRole(
            @PathVariable String roleId, @PathVariable String permissionId) {
        roleService.removePermissionFromRole(roleId, permissionId);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_PERMISSION_REMOVE_SUCCESS))
                .build();
    }
}
