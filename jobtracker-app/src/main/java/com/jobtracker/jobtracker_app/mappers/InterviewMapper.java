package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.interview.InterviewCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.interview.InterviewUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.InterviewResponse;
import com.jobtracker.jobtracker_app.entities.Interview;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface InterviewMapper {
    Interview toInterview(InterviewCreationRequest request);

    @Mapping(source = "application.id", target = "applicationId")
    @Mapping(source = "job.id", target = "jobId")
    @Mapping(source = "company.id", target = "companyId")
    InterviewResponse toInterviewResponse(Interview interview);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateInterview(@MappingTarget Interview interview, InterviewUpdateRequest request);
}




