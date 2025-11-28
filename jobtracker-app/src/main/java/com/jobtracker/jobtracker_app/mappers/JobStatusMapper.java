package com.jobtracker.jobtracker_app.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.jobtracker.jobtracker_app.dto.requests.JobStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.JobStatusResponse;
import com.jobtracker.jobtracker_app.entities.JobStatus;

@Mapper(componentModel = "spring")
public interface JobStatusMapper {
    JobStatus toJobStatus(JobStatusRequest request);

    JobStatusResponse toJobStatusResponse(JobStatus jobStatus);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateJobStatus(@MappingTarget JobStatus jobStatus, JobStatusRequest request);
}

