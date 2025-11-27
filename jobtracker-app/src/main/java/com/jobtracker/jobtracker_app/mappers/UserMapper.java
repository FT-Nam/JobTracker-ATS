package com.jobtracker.jobtracker_app.mappers;

import org.mapstruct.*;

import com.jobtracker.jobtracker_app.dto.requests.UserCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.UserUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.UserResponse;
import com.jobtracker.jobtracker_app.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    @Mapping(target = "roleName", source = "role.name")
    UserResponse toUserResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
