package com.jobtracker.jobtracker_app.mappers;

import org.mapstruct.*;

import com.jobtracker.jobtracker_app.dto.requests.permission.PermissionRequest;
import com.jobtracker.jobtracker_app.dto.responses.permission.PermissionResponse;
import com.jobtracker.jobtracker_app.entities.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    @Mapping(target = "name", ignore = true)
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePermission(@MappingTarget Permission permission, PermissionRequest request);
}
