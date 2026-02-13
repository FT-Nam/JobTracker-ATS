package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.ChangePasswordRequest;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.jobtracker.jobtracker_app.dto.requests.UserUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.user.UploadAvatarResponse;
import com.jobtracker.jobtracker_app.dto.responses.user.UserResponse;
import com.jobtracker.jobtracker_app.services.UserService;
import com.jobtracker.jobtracker_app.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.IOException;

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

    @PostMapping("/avatar")
    public ApiResponse<UploadAvatarResponse> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        return ApiResponse.<UploadAvatarResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.USER_AVATAR_UPLOAD_SUCCESS))
                .data(userService.uploadAvatar(file))
                .build();
    }
}
