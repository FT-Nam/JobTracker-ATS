package com.jobtracker.jobtracker_app.controllers;

import java.util.List;

import com.jobtracker.jobtracker_app.dto.requests.ChangePasswordRequest;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.jobtracker.jobtracker_app.dto.requests.UserCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.UserUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.UserResponse;
import com.jobtracker.jobtracker_app.services.UserService;
import com.jobtracker.jobtracker_app.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/users")
public class UserProfileController {
    UserService userService;
    LocalizationUtils localizationUtils;

    @GetMapping("/profile")
    public ApiResponse<UserResponse> getProfile() {
        return ApiResponse.<UserResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_PROFILE_SUCCESS))
                .data(userService.getProfile())
                .build();
    }

    @PutMapping("/profile/{id}")
    public ApiResponse<UserResponse> update(@PathVariable String id, @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_UPDATE_SUCCESS))
                .data(userService.update(id, request))
                .build();
    }

    @PutMapping("/change-password")
    public ApiResponse<UserResponse> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request);

        return ApiResponse.<UserResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_CHANGE_SUCCESS))
                .build();
    }

    // Upload Avatar đợi DropBox
}
