package com.jobtracker.jobtracker_app.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.jobtracker.jobtracker_app.dto.requests.InterviewResultRequest;
import com.jobtracker.jobtracker_app.dto.responses.InterviewResultResponse;
import com.jobtracker.jobtracker_app.entities.InterviewResult;

@Mapper(componentModel = "spring")
public interface InterviewResultMapper {
    InterviewResult toInterviewResult(InterviewResultRequest request);

    InterviewResultResponse toInterviewResultResponse(InterviewResult interviewResult);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateInterviewResult(@MappingTarget InterviewResult interviewResult, InterviewResultRequest request);
}

