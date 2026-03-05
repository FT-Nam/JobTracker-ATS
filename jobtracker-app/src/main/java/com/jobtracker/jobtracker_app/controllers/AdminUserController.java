package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.user.UserCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.user.UserInviteRequest;
import com.jobtracker.jobtracker_app.dto.requests.user.UserUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.PaginationInfo;
import com.jobtracker.jobtracker_app.dto.responses.user.UserResponse;
import com.jobtracker.jobtracker_app.services.AdminUserService;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/admin/users")
public class AdminUserController {
    AdminUserService adminUserService;
    LocalizationUtils localizationUtils;

    @PostMapping("/employees")
    public ApiResponse<UserResponse> addEmployee(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EMPLOYEE_ADD_SUCCESS))
                .data(adminUserService.addEmployee(request))
                .build();
    }

    @PostMapping("/invite")
    public ApiResponse<Void> inviteUser(@RequestBody @Valid UserInviteRequest request) {
        adminUserService.inviteUser(request);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_INVITE_SUCCESS))
                .build();
    }

    @PostMapping("/{userId}/resend-invite")
    public ApiResponse<Void> resendInvite(@PathVariable String userId) {
        adminUserService.resendInvite(userId);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_INVITE_RESEND_SUCCESS))
                .build();
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String roleId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Boolean emailVerified,
            Pageable pageable) {

        Page<UserResponse> userResponses = adminUserService.getAll(keyword, roleId, isActive, emailVerified, pageable);

        return ApiResponse.<List<UserResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_LIST_SUCCESS))
                .data(userResponses.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(userResponses.getNumber())
                        .size(userResponses.getSize())
                        .totalElements(userResponses.getTotalElements())
                        .build())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(@PathVariable String id) {
        return ApiResponse.<UserResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_DETAIL_SUCCESS))
                .data(adminUserService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> update(@PathVariable String id, @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_UPDATE_SUCCESS))
                .data(adminUserService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        adminUserService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_DELETE_SUCCESS))
                .build();
    }

    @PatchMapping("/{id}/restore")
    public ApiResponse<Void> restore(@PathVariable String id) {
        adminUserService.restore(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_RESTORE_SUCCESS))
                .build();
    }
}
