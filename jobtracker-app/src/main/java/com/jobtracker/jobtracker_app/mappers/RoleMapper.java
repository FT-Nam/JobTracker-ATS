package com.jobtracker.jobtracker_app.mappers;

import org.mapstruct.*;

import com.jobtracker.jobtracker_app.dto.requests.RoleRequest;
import com.jobtracker.jobtracker_app.dto.responses.RoleResponse;
import com.jobtracker.jobtracker_app.entities.Role;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "rolePermissions", ignore = true)
    Role toRole(RoleRequest request);

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "permissionIds", expression = "java(mapPermissionIds(role))")
    RoleResponse toRoleResponse(Role role);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "rolePermissions", ignore = true)
    void updateRole(@MappingTarget Role role, RoleRequest request);

    default Set<String> mapPermissionIds(Role role) {
        if (role.getRolePermissions() == null) {
            return Set.of();
        }
        return role.getRolePermissions().stream()
                .filter(rp -> !rp.getIsDeleted())
                .map(rp -> rp.getPermission().getId())
                .collect(Collectors.toSet());
    }
}
