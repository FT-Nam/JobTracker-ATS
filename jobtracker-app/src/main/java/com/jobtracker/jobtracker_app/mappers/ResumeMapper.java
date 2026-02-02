package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.ResumeRequest;
import com.jobtracker.jobtracker_app.dto.responses.ResumeResponse;
import com.jobtracker.jobtracker_app.entities.Resume;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ResumeMapper {
    @Mapping(target = "user", ignore = true)
    Resume toResume(ResumeRequest request);

    @Mapping(source = "user.id", target = "userId")
    ResumeResponse toResumeResponse(Resume resume);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    void updateResume(@MappingTarget Resume resume, ResumeRequest request);
}




