package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.job.JobCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.job.JobUpdateRequest;
import com.jobtracker.jobtracker_app.dto.requests.job.JobUpdateStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.job.JobResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.JobSkillCreationResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.JobSummaryResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.JobUpdateResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.JobUpdateStatusResponse;
import com.jobtracker.jobtracker_app.entities.Job;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface JobMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "company", ignore = true)
    Job toJob(JobCreationRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "company.id", target = "companyId")
    JobResponse toJobResponse(Job job);

    JobSummaryResponse toJobSummaryResponse(Job job);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateJob(@MappingTarget Job job, JobUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateStatusJob(@MappingTarget Job job, JobUpdateStatusRequest request);

    JobUpdateResponse toJobUpdateResponse(Job job);

    JobUpdateStatusResponse toJobUpdateStatusResponse(Job job);
}




