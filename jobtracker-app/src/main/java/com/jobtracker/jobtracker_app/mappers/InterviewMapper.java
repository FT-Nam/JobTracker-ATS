package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.interview.InterviewCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.interview.InterviewUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.interview.InterviewerResponse;
import com.jobtracker.jobtracker_app.dto.responses.interview.InterviewResponse;
import com.jobtracker.jobtracker_app.entities.Interview;
import com.jobtracker.jobtracker_app.entities.InterviewInterviewer;
import com.jobtracker.jobtracker_app.entities.User;
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

    default InterviewerResponse toInterviewerResponse(InterviewInterviewer ii) {
        if (ii == null) return null;
        User u = ii.getInterviewer();
        if (u == null) return null;
        String name = (u.getFirstName() != null ? u.getFirstName() : "").trim() + " "
                + (u.getLastName() != null ? u.getLastName() : "").trim();
        if (name.isBlank()) name = null;
        return InterviewerResponse.builder()
                .id(u.getId())
                .name(name)
                .email(u.getEmail())
                .isPrimary(ii.getIsPrimary())
                .build();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateInterview(@MappingTarget Interview interview, InterviewUpdateRequest request);
}




