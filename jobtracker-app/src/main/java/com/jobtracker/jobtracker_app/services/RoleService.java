package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.role.RoleCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.role.RolePermissionRequest;
import com.jobtracker.jobtracker_app.dto.requests.role.RolePermissionsRequest;
import com.jobtracker.jobtracker_app.dto.requests.role.RoleUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.role.RolePermissionsResponse;
import com.jobtracker.jobtracker_app.dto.responses.role.RolePermissionsUpdateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobtracker.jobtracker_app.dto.responses.role.RoleResponse;

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
