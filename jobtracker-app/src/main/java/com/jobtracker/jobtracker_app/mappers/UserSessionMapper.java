package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.UserSessionRequest;
import com.jobtracker.jobtracker_app.dto.responses.UserSessionResponse;
import com.jobtracker.jobtracker_app.entities.UserSession;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserSessionMapper {
    @Mapping(target = "user", ignore = true)
    UserSession toUserSession(UserSessionRequest request);

    @Mapping(source = "user.id", target = "userId")
    UserSessionResponse toUserSessionResponse(UserSession userSession);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    void updateUserSession(@MappingTarget UserSession userSession, UserSessionRequest request);
}




