package com.jobtracker.jobtracker_app.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.jobtracker.jobtracker_app.dto.requests.RoleRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.PaginationInfo;
import com.jobtracker.jobtracker_app.dto.responses.RoleResponse;
import com.jobtracker.jobtracker_app.services.RoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/role")
public class RoleController {
    RoleService roleService;

    @PostMapping
    public ApiResponse<RoleResponse> create(@RequestBody @Valid RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .message("Role create successfully")
                .data(roleService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAll(Pageable pageable) {
        Page<RoleResponse> responses = roleService.getAll(pageable);
        return ApiResponse.<List<RoleResponse>>builder()
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
        return ApiResponse.<RoleResponse>builder().data(roleService.getById(id)).build();
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleResponse> update(@PathVariable String id, @RequestBody @Valid RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .message("Role update successfully")
                .data(roleService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        roleService.delete(id);
        return ApiResponse.<Void>builder().message("Role delete successfully").build();
    }
}
