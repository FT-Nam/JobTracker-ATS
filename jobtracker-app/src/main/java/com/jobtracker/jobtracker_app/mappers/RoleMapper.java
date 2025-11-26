package com.jobtracker.jobtracker_app.mappers;

import org.mapstruct.*;

import com.jobtracker.jobtracker_app.dto.requests.RoleRequest;
import com.jobtracker.jobtracker_app.dto.responses.RoleResponse;
import com.jobtracker.jobtracker_app.entities.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "name", ignore = true)
    Role toRole(RoleRequest request);

    @Mapping(target = "name", ignore = true)
    RoleResponse toRoleResponse(Role role);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRole(@MappingTarget Role role, RoleRequest request);
}
