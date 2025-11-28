package com.jobtracker.jobtracker_app.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.jobtracker.jobtracker_app.dto.requests.JobTypeRequest;
import com.jobtracker.jobtracker_app.dto.responses.JobTypeResponse;
import com.jobtracker.jobtracker_app.entities.JobType;

@Mapper(componentModel = "spring")
public interface JobTypeMapper {
    JobType toJobType(JobTypeRequest request);

    JobTypeResponse toJobTypeResponse(JobType jobType);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateJobType(@MappingTarget JobType jobType, JobTypeRequest request);
}

