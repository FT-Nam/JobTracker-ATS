package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.ChangePasswordRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobtracker.jobtracker_app.dto.requests.UserCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.UserUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.UserResponse;
import org.springframework.data.repository.query.Param;

public interface UserService {
    UserResponse create(UserCreationRequest request);

    UserResponse getById(String id);

    UserResponse getProfile();

    Page<UserResponse> getAll(String keyword, String roleId,
                              Boolean isActive, Boolean emailVerified, Pageable pageable);

    UserResponse update(String id, UserUpdateRequest request);

    UserResponse updateProfile(UserUpdateRequest request);

    void changePassword(ChangePasswordRequest request);

    void delete(String id);

    void restore(String id);

    // Upload Avatar ...

}
