package com.jobtracker.jobtracker_app.mapper;

import org.mapstruct.*;

import com.jobtracker.jobtracker_app.dto.request.RoleRequest;
import com.jobtracker.jobtracker_app.dto.response.RoleResponse;
import com.jobtracker.jobtracker_app.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "name", ignore = true)
    Role toRole(RoleRequest request);

    @Mapping(target = "name", ignore = true)
    RoleResponse toRoleResponse(Role role);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRole(@MappingTarget Role role, RoleRequest request);
}
