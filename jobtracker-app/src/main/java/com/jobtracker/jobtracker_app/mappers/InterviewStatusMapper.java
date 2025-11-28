package com.jobtracker.jobtracker_app.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.jobtracker.jobtracker_app.dto.requests.InterviewStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.InterviewStatusResponse;
import com.jobtracker.jobtracker_app.entities.InterviewStatus;

@Mapper(componentModel = "spring")
public interface InterviewStatusMapper {
    InterviewStatus toInterviewStatus(InterviewStatusRequest request);

    InterviewStatusResponse toInterviewStatusResponse(InterviewStatus interviewStatus);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateInterviewStatus(@MappingTarget InterviewStatus interviewStatus, InterviewStatusRequest request);
}

