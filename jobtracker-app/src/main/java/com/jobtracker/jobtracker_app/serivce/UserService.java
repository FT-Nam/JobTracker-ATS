package com.jobtracker.jobtracker_app.serivce;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobtracker.jobtracker_app.dto.request.UserCreationRequest;
import com.jobtracker.jobtracker_app.dto.request.UserUpdateRequest;
import com.jobtracker.jobtracker_app.dto.response.UserResponse;

public interface UserService {
    UserResponse create(UserCreationRequest request);

    UserResponse getById(String id);

    Page<UserResponse> getAll(Pageable pageable);

    UserResponse update(String id, UserUpdateRequest request);

    void delete(String id);
}
