package com.jobtracker.jobtracker_app.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.jobtracker_app.dto.requests.notification.NotificationRequest;
import com.jobtracker.jobtracker_app.dto.responses.notification.NotificationResponse;
import com.jobtracker.jobtracker_app.entities.Notification;
import org.mapstruct.*;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "job", ignore = true)
    @Mapping(target = "application", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    Notification toNotification(NotificationRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "job.id", target = "jobId")
    @Mapping(source = "application.id", target = "applicationId")
    @Mapping(target = "metadata", ignore = true) // xử lý sau
    NotificationResponse toNotificationResponse(
            Notification notification,
            @Context ObjectMapper objectMapper
    );

    @AfterMapping
    default void setMetadata(
            @MappingTarget NotificationResponse target,
            Notification notification,
            @Context ObjectMapper objectMapper
    ) {
        if (notification.getMetadata() == null) {
            return;
        }

        try {
            Map<String, Object> metadata = objectMapper.readValue(notification.getMetadata(), Map.class);

            target.setMetadata(metadata);
        } catch (Exception ignored) {
        }
    }
}




