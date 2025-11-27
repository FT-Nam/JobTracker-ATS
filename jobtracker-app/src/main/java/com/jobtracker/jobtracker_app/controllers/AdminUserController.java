package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.UserCreationRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.PaginationInfo;
import com.jobtracker.jobtracker_app.dto.responses.UserResponse;
import com.jobtracker.jobtracker_app.services.UserService;
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
    UserService userService;
    LocalizationUtils localizationUtils;

    @PostMapping
    public ApiResponse<UserResponse> create(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_CREATE_SUCCESS))
                .data(userService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAll(Pageable pageable) {
        Page<UserResponse> userResponses = userService.getAll(pageable);
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
                .data(userService.getById(id))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        userService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_DELETE_SUCCESS))
                .build();
    }

    @PatchMapping("/{id}/restore")
    public ApiResponse<Void> restore(@PathVariable String id) {
        userService.restore(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_RESTORE_SUCCESS))
                .build();
    }
}
