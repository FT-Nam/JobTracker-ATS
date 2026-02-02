package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.NotificationRequest;
import com.jobtracker.jobtracker_app.dto.responses.NotificationResponse;
import com.jobtracker.jobtracker_app.entities.Notification;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "job", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "priority", ignore = true)
    Notification toNotification(NotificationRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "job.id", target = "jobId")
    @Mapping(source = "type.id", target = "typeId")
    @Mapping(source = "priority.id", target = "priorityId")
    NotificationResponse toNotificationResponse(Notification notification);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "job", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "priority", ignore = true)
    void updateNotification(@MappingTarget Notification notification, NotificationRequest request);
}




