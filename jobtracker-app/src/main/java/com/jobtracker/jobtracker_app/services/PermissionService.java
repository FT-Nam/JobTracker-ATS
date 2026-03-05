package com.jobtracker.jobtracker_app.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobtracker.jobtracker_app.dto.requests.permission.PermissionRequest;
import com.jobtracker.jobtracker_app.dto.responses.permission.PermissionResponse;

public interface PermissionService {
    PermissionResponse create(PermissionRequest request);

    PermissionResponse getById(String id);

    Page<PermissionResponse> getAll(Pageable pageable);

    PermissionResponse update(String id, PermissionRequest request);

    void delete(String id);
}
