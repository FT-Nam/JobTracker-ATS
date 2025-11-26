package com.jobtracker.jobtracker_app.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobtracker.jobtracker_app.dto.requests.RoleRequest;
import com.jobtracker.jobtracker_app.dto.responses.RoleResponse;

public interface RoleService {
    RoleResponse create(RoleRequest request);

    RoleResponse getById(String id);

    Page<RoleResponse> getAll(Pageable pageable);

    RoleResponse update(String id, RoleRequest request);

    void delete(String id);
}
