package com.jobtracker.jobtracker_app.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.jobtracker.jobtracker_app.dto.requests.InterviewTypeRequest;
import com.jobtracker.jobtracker_app.dto.responses.InterviewTypeResponse;
import com.jobtracker.jobtracker_app.entities.InterviewType;

@Mapper(componentModel = "spring")
public interface InterviewTypeMapper {
    InterviewType toInterviewType(InterviewTypeRequest request);

    InterviewTypeResponse toInterviewTypeResponse(InterviewType interviewType);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateInterviewType(@MappingTarget InterviewType interviewType, InterviewTypeRequest request);
}

