package com.jobtracker.jobtracker_app.controllers;

import java.util.List;

import com.jobtracker.jobtracker_app.dto.requests.ChangePasswordRequest;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.jobtracker.jobtracker_app.dto.requests.UserCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.UserUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.PaginationInfo;
import com.jobtracker.jobtracker_app.dto.responses.UserResponse;
import com.jobtracker.jobtracker_app.services.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/users")
public class UserProfileController {
    UserService userService;

    @GetMapping("/profile")
    public ApiResponse<UserResponse> getProfile() {
        return ApiResponse.<UserResponse>builder().data(userService.getProfile()).build();
    }

    @PutMapping("/profile/{id}")
    public ApiResponse<UserResponse> update(@PathVariable String id, @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .message("User update successfully")
                .data(userService.update(id, request))
                .build();
    }

    @PutMapping("/profile/{id}")
    public ApiResponse<UserResponse> changePassword(@PathVariable String id,
                                                    @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(id, request);

        return ApiResponse.<UserResponse>builder()
                .message("Password changed successfully")
                .build();
    }

    // Upload Avatar đợi DropBox
}
