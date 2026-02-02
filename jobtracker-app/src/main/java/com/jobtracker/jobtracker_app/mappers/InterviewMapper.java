package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.InterviewRequest;
import com.jobtracker.jobtracker_app.dto.responses.InterviewResponse;
import com.jobtracker.jobtracker_app.entities.Interview;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface InterviewMapper {
    @Mapping(target = "job", ignore = true)
    @Mapping(target = "interviewType", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "result", ignore = true)
    Interview toInterview(InterviewRequest request);

    @Mapping(source = "job.id", target = "jobId")
    @Mapping(source = "interviewType.id", target = "interviewTypeId")
    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "result.id", target = "resultId")
    InterviewResponse toInterviewResponse(Interview interview);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "job", ignore = true)
    @Mapping(target = "interviewType", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "result", ignore = true)
    void updateInterview(@MappingTarget Interview interview, InterviewRequest request);
}




