package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.ApplicationStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.ApplicationStatusResponse;
import com.jobtracker.jobtracker_app.entities.ApplicationStatus;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ApplicationStatusMapper {
    ApplicationStatus toApplicationStatus(ApplicationStatusRequest request);

    ApplicationStatusResponse toApplicationStatusResponse(ApplicationStatus applicationStatus);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateApplicationStatus(@MappingTarget ApplicationStatus applicationStatus, ApplicationStatusRequest request);
}

