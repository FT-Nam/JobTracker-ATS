package com.jobtracker.jobtracker_app.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.jobtracker.jobtracker_app.dto.requests.NotificationTypeRequest;
import com.jobtracker.jobtracker_app.dto.responses.NotificationTypeResponse;
import com.jobtracker.jobtracker_app.entities.NotificationType;

@Mapper(componentModel = "spring")
public interface NotificationTypeMapper {
    NotificationType toNotificationType(NotificationTypeRequest request);

    NotificationTypeResponse toNotificationTypeResponse(NotificationType notificationType);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateNotificationType(@MappingTarget NotificationType notificationType, NotificationTypeRequest request);
}

