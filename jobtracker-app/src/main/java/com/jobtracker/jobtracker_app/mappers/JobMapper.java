package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.job.JobCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.job.JobUpdateRequest;
import com.jobtracker.jobtracker_app.dto.requests.job.JobUpdateStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.job.JobResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.JobSkillCreationResponse;
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
    @Mapping(target = "jobType", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "experienceLevel", ignore = true)
    Job toJob(JobCreationRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "jobType.id", target = "jobTypeId")
    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "priority.id", target = "priorityId")
    @Mapping(source = "experienceLevel.id", target = "experienceLevelId")
    JobResponse toJobResponse(Job job);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "status", ignore = true)
    void updateJob(@MappingTarget Job job, JobUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "status", ignore = true)
    void updateStatusJob(@MappingTarget Job job, JobUpdateStatusRequest request);

    @Mapping(source = "status.name", target = "status")
    JobUpdateResponse toJobUpdateResponse(Job job);

    @Mapping(source = "status.name", target = "status")
    JobUpdateStatusResponse toJobUpdateStatusResponse(Job job);
}




