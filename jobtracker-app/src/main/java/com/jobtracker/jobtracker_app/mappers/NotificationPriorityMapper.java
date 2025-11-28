package com.jobtracker.jobtracker_app.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.jobtracker.jobtracker_app.dto.requests.NotificationPriorityRequest;
import com.jobtracker.jobtracker_app.dto.responses.NotificationPriorityResponse;
import com.jobtracker.jobtracker_app.entities.NotificationPriority;

@Mapper(componentModel = "spring")
public interface NotificationPriorityMapper {
    NotificationPriority toNotificationPriority(NotificationPriorityRequest request);

    NotificationPriorityResponse toNotificationPriorityResponse(NotificationPriority notificationPriority);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateNotificationPriority(
            @MappingTarget NotificationPriority notificationPriority, NotificationPriorityRequest request);
}

