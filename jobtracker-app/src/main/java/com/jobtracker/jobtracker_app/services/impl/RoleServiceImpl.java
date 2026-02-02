package com.jobtracker.jobtracker_app.services.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_app.dto.requests.RoleRequest;
import com.jobtracker.jobtracker_app.dto.responses.RoleResponse;
import com.jobtracker.jobtracker_app.entities.Permission;
import com.jobtracker.jobtracker_app.entities.Role;
import com.jobtracker.jobtracker_app.entities.RolePermission;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.RoleMapper;
import com.jobtracker.jobtracker_app.repositories.PermissionRepository;
import com.jobtracker.jobtracker_app.repositories.RolePermissionRepository;
import com.jobtracker.jobtracker_app.repositories.RoleRepository;
import com.jobtracker.jobtracker_app.repositories.UserRepository;
import com.jobtracker.jobtracker_app.services.RoleService;
import com.jobtracker.jobtracker_app.services.cache.PermissionCacheService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionRepository permissionRepository;
    RolePermissionRepository rolePermissionRepository;
    PermissionCacheService permissionCacheService;
    UserRepository userRepository;

    @Override
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    @Transactional
    public RoleResponse create(RoleRequest request) {
        validateNameUnique(request.getName(), null);

        Role role = roleMapper.toRole(request);
        role.setName(request.getName());
        role = roleRepository.save(role);

        List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
        Role finalRole = role;
        List<RolePermission> rolePermissions = permissions.stream()
                .map(permission -> RolePermission.builder()
                        .role(finalRole)
                        .permission(permission)
                        .build())
                .toList();
        rolePermissionRepository.saveAll(rolePermissions);

        return roleMapper.toRoleResponse(role);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public RoleResponse getById(String id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        return roleMapper.toRoleResponse(role);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public Page<RoleResponse> getAll(Pageable pageable) {
        return roleRepository.findAll(pageable).map(roleMapper::toRoleResponse);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    @Transactional
    public RoleResponse update(String id, RoleRequest request) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        if (request.getName() != null && !request.getName().equals(role.getName())) {
            validateNameUnique(request.getName(), id);
        }

        if (request.getPermissionIds() != null) {
            List<RolePermission> existingRolePermissions = rolePermissionRepository.findByRoleId(role.getId());
            existingRolePermissions.forEach(RolePermission::softDelete);
            rolePermissionRepository.saveAll(existingRolePermissions);

            List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
            List<RolePermission> newRolePermissions = permissions.stream()
                    .map(permission -> RolePermission.builder()
                            .role(role)
                            .permission(permission)
                            .build())
                    .toList();
            rolePermissionRepository.saveAll(newRolePermissions);

            List<String> userIds = userRepository.findAllByRoleId(role.getId()).stream()
                    .map(User::getId)
                    .toList();
            userIds.forEach(permissionCacheService::evict);
        }

        roleMapper.updateRole(role, request);
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    @Transactional
    public void delete(String id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        role.softDelete();
        roleRepository.save(role);
    }

    private void validateNameUnique(String name, String excludeId) {
        boolean exists = excludeId == null
                ? roleRepository.existsByNameIgnoreCase(name)
                : roleRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId);
        if (exists) {
            throw new AppException(ErrorCode.NAME_EXISTED);
        }
    }
}
