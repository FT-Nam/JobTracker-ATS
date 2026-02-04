package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.RoleCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.RolePermissionRequest;
import com.jobtracker.jobtracker_app.dto.requests.RolePermissionsRequest;
import com.jobtracker.jobtracker_app.dto.requests.RoleUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.RolePermissionsResponse;
import com.jobtracker.jobtracker_app.dto.responses.RolePermissionsUpdateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobtracker.jobtracker_app.dto.responses.RoleResponse;

public interface RoleService {
    RoleResponse create(RoleCreationRequest request);

    RoleResponse getById(String id);

    Page<RoleResponse> getAll(Pageable pageable);

    RoleResponse update(String id, RoleUpdateRequest request);

    void delete(String id);

    Page<RolePermissionsResponse> getRolePermissions(String roleId, Pageable pageable);

    RolePermissionsUpdateResponse updateRolePermissions(String roleId, RolePermissionsRequest request);

    void addPermissionToRole(String roleId, RolePermissionRequest request);

    void removePermissionFromRole(String roleId, String permissionId);

}
