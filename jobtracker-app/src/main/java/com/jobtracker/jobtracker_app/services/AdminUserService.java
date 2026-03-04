package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.UserCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.UserInviteRequest;
import com.jobtracker.jobtracker_app.dto.requests.UserUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {
    UserResponse addEmployee(UserCreationRequest request);

    UserResponse getById(String id);

    Page<UserResponse> getAll(String keyword, String roleId,
                              Boolean isActive, Boolean emailVerified, Pageable pageable);

    UserResponse update(String id, UserUpdateRequest request);

    void delete(String id);

    void restore(String id);

    void inviteUser(UserInviteRequest request);

    void resendInvite(String userId);
}
