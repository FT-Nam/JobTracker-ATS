package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.responses.application.ApplicationStatusHistoryResponse;
import com.jobtracker.jobtracker_app.dto.responses.application.StatusHistory;
import com.jobtracker.jobtracker_app.entities.ApplicationStatus;
import com.jobtracker.jobtracker_app.entities.ApplicationStatusHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationStatusHistoryMapper {

    @Mapping(target = "applicationId", source = "application.id")
    @Mapping(target = "fromStatusId", source = "fromStatus.id")
    @Mapping(target = "fromStatus", source = "fromStatus")
    @Mapping(target = "toStatusId", source = "toStatus.id")
    @Mapping(target = "toStatus", source = "toStatus")
    @Mapping(target = "changedBy", source = "changedBy.id")
    @Mapping(target = "changedByName", source = "changedBy.lastName")
    ApplicationStatusHistoryResponse toResponse(ApplicationStatusHistory applicationStatusHistory);

    StatusHistory toStatusHistory(ApplicationStatus status);
}
