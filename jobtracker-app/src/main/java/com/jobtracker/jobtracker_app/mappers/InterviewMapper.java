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
    @Mapping(target = "application", ignore = true)
    @Mapping(target = "job", ignore = true)
    @Mapping(target = "company", ignore = true)
    Interview toInterview(InterviewRequest request);

    @Mapping(source = "application.id", target = "applicationId")
    @Mapping(source = "job.id", target = "jobId")
    @Mapping(source = "company.id", target = "companyId")
    InterviewResponse toInterviewResponse(Interview interview);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "application", ignore = true)
    @Mapping(target = "job", ignore = true)
    @Mapping(target = "company", ignore = true)
    void updateInterview(@MappingTarget Interview interview, InterviewRequest request);
}




