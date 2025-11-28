package com.jobtracker.jobtracker_app.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.jobtracker.jobtracker_app.dto.requests.PriorityRequest;
import com.jobtracker.jobtracker_app.dto.responses.PriorityResponse;
import com.jobtracker.jobtracker_app.entities.Priority;

@Mapper(componentModel = "spring")
public interface PriorityMapper {
    Priority toPriority(PriorityRequest request);

    PriorityResponse toPriorityResponse(Priority priority);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePriority(@MappingTarget Priority priority, PriorityRequest request);
}

